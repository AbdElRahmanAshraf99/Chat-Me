package com.chatme.controller;


import com.chatme.Utils.ObjectChecker;
import com.chatme.domain.ChatRoom;
import com.chatme.domain.ChatRoomType;
import com.chatme.domain.User;
import com.chatme.repository.ChatRoomRepository;
import com.chatme.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.chatme.controller.UserController.validateRequiredFieldsAndAppendMsgIfNeeded;

@RestController
@RequestMapping("/chat")
public class ChatRoomController {
    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    UserRepository userRepository;

    /***
     *
     * @param user_Id        Long: creator of the group must be registered user  //required param//
     * @param friend_Id      Long:  id of the member   must be registered user   //required param//
     * @param room_Name      String : the name of the room                       //required param//
     * @param description    String: description of the room
     * @return ResponseEntity<String> with body confirming creation and  a Header with the id of the room
     */
    @PostMapping(value = "/new-private-room")
    public ResponseEntity<String> NewRoom(@RequestParam Long user_Id, @RequestParam Long friend_Id, @RequestParam String room_Name, @RequestParam String description) {
        //Checking Valid Entries
        validateRequiredFieldsAndAppendMsgIfNeeded("user_Id", user_Id.toString());
        validateRequiredFieldsAndAppendMsgIfNeeded("friend_Id", friend_Id.toString());
        validateRequiredFieldsAndAppendMsgIfNeeded("room_Name", room_Name);

        //Validating USer ID is in Repo
        User realUser = userRepository.findById(user_Id).orElse(null);
        if (realUser == null)
            return ResponseEntity.badRequest().body("Wrong User Id");

        //Validating friend ID is in Repo
        User friend = userRepository.findById(friend_Id).orElse(null);
        if (friend == null)
            return ResponseEntity.badRequest().body("Wrong User Id");

        //creating and saving chat room
        ChatRoom room = new ChatRoom();
        room.setName(room_Name);
        room.setType(ChatRoomType.Private);
        room.setDescription(description);
        room.getMembers().addAll(List.of(realUser, friend));
        room.getAdmins().addAll(List.of(realUser, friend));
        chatRoomRepository.save(room);

        //updating the user and friend
        realUser.getChatRooms().add(room);
        userRepository.save(realUser);
        friend.getChatRooms().add(room);
        userRepository.save(friend);

        return ResponseEntity.ok().header("Room-ID", room.getId().toString()).body("Chat Room" + room.getName() + "is Created");
    }

    /***
     * creates  anew group from 1 user to as many users as desired
     * if @param friends_Ids is null the group has only one user
     * @param user_Id       Long: creator of the group  must be registered user //required param//
     * @param friends_Ids   Long array:  ids of the members
     * @param room_Name     String : the name of the room                       //required param//
     * @param description   String: description of the room
     * @return ResponseEntity<String> with body confirming creation and  a Header with the id of the room
     */
    @PostMapping(value = "/new-Group")
    public ResponseEntity<String> NewGroup(@RequestParam Long user_Id, @RequestParam Long[] friends_Ids, @RequestParam String room_Name, @RequestParam String description) {
        //Checking Valid Entries
        validateRequiredFieldsAndAppendMsgIfNeeded("user_Id", user_Id.toString());
        validateRequiredFieldsAndAppendMsgIfNeeded("room_Name", room_Name);

        //Validating USer ID is in Repo
        User realUser = userRepository.findById(user_Id).orElse(null);
        if (realUser == null)
            return ResponseEntity.badRequest().body("Wrong User Id");

        //Validating friends IDs is in Repo
        List<User> members = new ArrayList<>();
        for (Long id : friends_Ids
        ) {
            if (id == null)
                continue;
            User friend = userRepository.findById(id).orElse(null);
            if (friend == null)
                return ResponseEntity.badRequest().body("Wrong User Id");
            members.add(friend);
        }
        members.add(realUser);

        //creating and saving chat room
        ChatRoom room = new ChatRoom();
        room.setName(room_Name);
        room.setType(ChatRoomType.Group);
        room.setDescription(description);
        room.getMembers().addAll(members);
        room.getAdmins().add(realUser);
        chatRoomRepository.save(room);

        //updating the members chat rooms
        for (User member : members
        ) {
            member.getChatRooms().add(room);
            userRepository.save(member);

        }

        return ResponseEntity.ok().header("Room-ID", room.getId().toString()).body("Chat Room: " + room.getName() + " is Created");
    }

    /***
     * gets all chat room in the repo
     * @return ResponseEntity<String>: containing list of all ChatRoom Objects in chatRoomRepository
     */
    @GetMapping(value = "/get-all")
    public ResponseEntity<List<ChatRoom>> GetAllRooms() {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body((List<ChatRoom>) chatRoomRepository.findAll());
    }

    /***
     * adds  user to a room
     * @param room_id Long: id of the room to be added to           //required param//
     * @param user_id Long: id of the user to be added              //required param//
     * @return
     */
    @PatchMapping(value = "/add-user")
    public ResponseEntity<String> AdUserToRoom(@RequestParam Long room_id, @RequestParam Long user_id) {
        ChatRoom room = chatRoomRepository.findById(room_id).orElse(null);
        User user = userRepository.findById(user_id).orElse(null);
        if (ObjectChecker.isEmptyOrNull(room))
            return ResponseEntity.badRequest().body("room doesn't exist");
        if (ObjectChecker.isEmptyOrNull(user))
            return ResponseEntity.badRequest().body("user doesn't exist");
        room.getMembers().add(user);
        chatRoomRepository.save(room);
        user.getChatRooms().add(room);
        userRepository.save(user);
        return ResponseEntity.ok("user: " + user.getUsername() + " was added succesfully to room: " + room.getName());
    }

    /***
     * removes a user from chat a room requiers an admin id
     * @param admin_id Long : admin id that have remove privilages              //required param//
     * @param room_id Long : room id which The user and the admins are in       //required param//
     * @param user_id Long : user id to be removed                              //required param//
     * @return ResponseEntity<String>
     */

    @PatchMapping(value = "/remove-user")
    public ResponseEntity<String> RemoveUSerFromRoom(@RequestParam Long admin_id, @RequestParam Long room_id, @RequestParam Long user_id) {
        ChatRoom room = chatRoomRepository.findById(room_id).orElse(null);
        User admin = userRepository.findById(admin_id).orElse(null);
        User user = userRepository.findById(user_id).orElse(null);
        if (ObjectChecker.isEmptyOrNull(room))
            return ResponseEntity.badRequest().body("room doesn't exist");
        if (ObjectChecker.isEmptyOrNull(user))
            return ResponseEntity.badRequest().body("user doesn't exist");
        if (ObjectChecker.isEmptyOrNull(admin))
            return ResponseEntity.badRequest().body("user doesn't exist");

        if (room.getMembers().contains(user) && user.getChatRooms().contains(room)) {
            if (!room.getAdmins().contains(admin)) {
                return ResponseEntity.badRequest().body("u dont have privilage to delete user");
            }

            room.getMembers().remove(user);
            chatRoomRepository.save(room);
            user.getChatRooms().remove(room);
            userRepository.save(user);

        }
        return ResponseEntity.ok("user: " + user.getUsername() + " was removed succesfully from room: " + room.getName());
    }

    /***
     * deletes  a room from the repo and removing  all useres reqiuers an admin id
     * it removes the room from all the users chatrooms database --and vise versa removes the users from the room database then deletes it
     * @param admin_id id of one of the admins of the room
     * @param room_id id of the room to be deleted
     * @return ResponseEmtity<String> with the name of the room is deleted
     */
    @DeleteMapping(value = "/delete-room")
    public ResponseEntity<String> DeleteRoom(@RequestParam Long admin_id, @RequestParam Long room_id) {
        ChatRoom room = chatRoomRepository.findById(room_id).orElse(null);
        User admin = userRepository.findById(admin_id).orElse(null);
        if (ObjectChecker.isEmptyOrNull(room))
            return ResponseEntity.badRequest().body("room doesn't exist");
        if (ObjectChecker.isEmptyOrNull(admin))
            return ResponseEntity.badRequest().body("user doesn't exist");
        if (!admin.getChatRooms().contains(room))
            return ResponseEntity.badRequest().body("User: " + admin.getUsername() + " is not a member of the room");
        if (room.getAdmins().contains(admin) && admin.getChatRooms().contains(room)) {
            for (User member : room.getMembers()
            ) {
                member.getChatRooms().remove(room);
                userRepository.save(member);
            }
            room.getMembers().clear();
            room.getAdmins().clear();
            chatRoomRepository.delete(room);
        } else if (!room.getAdmins().contains(admin) && admin.getChatRooms().contains(room))
            return ResponseEntity.badRequest().body("User: " + admin.getUsername() + " is not an admin");


        return ResponseEntity.ok("Room : " + room.getName() + " Deleted Succesfully");
    }

    @GetMapping(value = "/find-room")
    public ResponseEntity<Object> FindRoomById(@RequestParam Long roomId) {
        if (ObjectChecker.isEmptyOrNull(roomId))
            return ResponseEntity.badRequest().body("");
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElse(null);
        if (chatRoom == null)
            return ResponseEntity.badRequest().body("Room was not found");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(chatRoom);
    }

}

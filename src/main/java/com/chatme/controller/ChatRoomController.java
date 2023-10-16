package com.chatme.controller;

import com.chatme.utils.ObjectChecker;
import com.chatme.domain.*;
import com.chatme.repository.GroupChatRoomRepository;
import com.chatme.repository.PrivateChatRoomRepository;
import com.chatme.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.chatme.controller.UserController.validateRequiredFieldsAndAppendMsgIfNeeded;

@RestController
@RequestMapping("/chat")
public class ChatRoomController
{
    @Autowired
    PrivateChatRoomRepository privateChatRoomRepository;
    @Autowired
    GroupChatRoomRepository groupChatRoomRepository;
    @Autowired
    UserRepository userRepository;

    /***
     *
     * @param userId        Long: creator of the group must be registered user  //required param//
     * @param friendId      Long:  id of the member   must be registered user   //required param//
     * @return ResponseEntity<String> with body confirming creation and  a Header with the id of the room
     */
    @PostMapping(value = "/new-private-room")
    public ResponseEntity<String> addNewPrivateChatRoom(@RequestParam Long userId, @RequestParam Long friendId)
    {
        //Checking Valid Entries
        validateRequiredFieldsAndAppendMsgIfNeeded("user_Id", userId.toString());
        validateRequiredFieldsAndAppendMsgIfNeeded("friend_Id", friendId.toString());
        //Validating User ID is in Repo
        User realUser = userRepository.findById(userId).orElse(null);
        if (realUser == null)
            return ResponseEntity.badRequest().body("Wrong User Id");
        //Validating friend ID is in Repo
        User friend = userRepository.findById(friendId).orElse(null);
        if (friend == null)
            return ResponseEntity.badRequest().body("Wrong User Id");
        //creating and saving chat room
        PrivateChatRoom room = new PrivateChatRoom();
        List<UserPrivateChatRooms> userPrivateChatRooms = new ArrayList<>();
        UserPrivateChatRooms chatRoomsUser = new UserPrivateChatRooms();
        chatRoomsUser.setUser(realUser);
        chatRoomsUser.setChatRoom(room);
        userPrivateChatRooms.add(chatRoomsUser);
        UserPrivateChatRooms chatRoomsUser2 = new UserPrivateChatRooms();
        chatRoomsUser2.setUser(friend);
        chatRoomsUser2.setChatRoom(room);
        userPrivateChatRooms.add(chatRoomsUser2);
        room.setUserPrivateChatRooms(userPrivateChatRooms);
        privateChatRoomRepository.save(room);

        return ResponseEntity.ok().body("Chat Room is Created");
    }

    /***
     * creates  anew group from 1 user to as many users as desired
     * if @param friends_Ids is null the group has only one user
     * @param userId       Long: creator of the group  must be registered user //required param//
     * @param friendsIds   Long array:  ids of the members
     * @param roomName     String : the name of the room                       //required param//
     * @param description   String: description of the room
     * @return ResponseEntity<String> with body confirming creation and  a Header with the id of the room
     */
    @PostMapping(value = "/new-Group")
    public ResponseEntity<String> NewGroup(@RequestParam Long userId, @RequestParam Long[] friendsIds, @RequestParam String roomName,
                                           @RequestParam String description)
    {
        //Checking Valid Entries
        validateRequiredFieldsAndAppendMsgIfNeeded("user_Id", userId.toString());
        validateRequiredFieldsAndAppendMsgIfNeeded("room_Name", roomName);

        //Validating USer ID is in Repo
        User creator = userRepository.findById(userId).orElse(null);
        if (creator == null)
            return ResponseEntity.badRequest().body("Wrong User Id");

        //creating  a list to fit all members and adding the creator of room
        List<User> members = new ArrayList<>();
        members.add(creator);
        //Validating friends IDs is in Repo and adding them in the members list
        List<Long> nonusers = new ArrayList<>();
        for (Long id : friendsIds)
        {
            if (id == null)
                continue;
            User friend = userRepository.findById(id).orElse(null);
            if (friend == null)
            {
                nonusers.add(id);
                continue;
            }
            members.add(friend);
        }


        //creating and saving chat room
        GroupChatRoom room = new GroupChatRoom();
        room.setName(roomName);
        room.setDescription(description);
        List<UserGroupChatRooms> userGroupChatRooms = new ArrayList<>();
        for (User member : members)
        {
            UserGroupChatRooms groupUser = new UserGroupChatRooms();
            groupUser.setIsAdmin(member.equals(creator));
            groupUser.setChatRoom(room);
            groupUser.setUser(member);
            userGroupChatRooms.add(groupUser);
        }
        room.setUserGroupChatRooms(userGroupChatRooms);
        groupChatRoomRepository.save(room);


        return ResponseEntity.ok().header("Room-ID", room.getId().toString()).body("Chat Room: " + room.getName() + " is Created \n Users: " + nonusers + " couldn't be added (are not useres)");
    }

    /***
     * gets all chat room in the repo
     * @return ResponseEntity<String>: containing list of all private ChatRoom Objects in chatRoomRepository
     */
    @GetMapping(value = "/get-all")
    public ResponseEntity<List<? extends AbsChatRoom>> GetAllRooms()
    {
        ArrayList<AbsChatRoom> rooms = new ArrayList<>();
        rooms.addAll((Collection<? extends AbsChatRoom>) privateChatRoomRepository.findAll());
        rooms.addAll((Collection<? extends AbsChatRoom>) groupChatRoomRepository.findAll());
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(rooms);
    }

    /***
     * gets all chat room in the repo
     * @return ResponseEntity<String>: containing list of all private ChatRoom Objects in chatRoomRepository
     */
    @GetMapping(value = "/get-all-private")
    public ResponseEntity<List<PrivateChatRoom>> GetAllPrivateRooms()
    {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body((List<PrivateChatRoom>) privateChatRoomRepository.findAll());
    }

    /***
     * gets all chat room in the repo
     * @return ResponseEntity<String>: containing list of all Group ChatRoom Objects in chatRoomRepository
     */
    @GetMapping(value = "/get-all-groups")
    public ResponseEntity<List<GroupChatRoom>> GetAllGroupRooms()
    {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body((List<GroupChatRoom>) groupChatRoomRepository.findAll());
    }

    /***
     * adds  user to a room
     * @param roomId Long: id of the room to be added to           //required param//
     * @param userId Long: id of the user to be added              //required param//
     * @return ResbonseEntity<String>  comtaining succes message
     */
    @PatchMapping(value = "/add-user")
    public ResponseEntity<String> AdUserToRoom(@RequestParam Long roomId, @RequestParam Long userId)
    {        //getting room from room repo and validating it also the user
        GroupChatRoom room = groupChatRoomRepository.findById(roomId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        if (ObjectChecker.isEmptyOrNull(room))
            return ResponseEntity.badRequest().body("room doesn't exist");
        if (ObjectChecker.isEmptyOrNull(user))
            return ResponseEntity.badRequest().body("user doesn't exist");
        //creating an instance of the room user relation table and updating it with the new user
        UserGroupChatRooms userGroupChatRooms = new UserGroupChatRooms();
        userGroupChatRooms.setUser(user);
        userGroupChatRooms.setChatRoom(room);
        groupChatRoomRepository.save(room);
        return ResponseEntity.ok("user: " + user.getUsername() + " was added successfully to room: " + room.getName());
    }

    /***
     * removes a user from chat a room requiers an admin id
     * @param adminId Long : admin id that have remove privilages              //required param//
     * @param roomId Long : room id which The user and the admins are in       //required param//
     * @param userId Long : user id to be removed                              //required param//
     * @return ResponseEntity<String>
     */

    @PatchMapping(value = "/remove-user")
    public ResponseEntity<String> RemoveUSerFromRoom(@RequestParam Long adminId, @RequestParam Long roomId, @RequestParam Long userId)
    {
        GroupChatRoom room = groupChatRoomRepository.findById(roomId).orElse(null);
        User admin = userRepository.findById(adminId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);
        if (ObjectChecker.isEmptyOrNull(room))
            return ResponseEntity.badRequest().body("room doesn't exist");
        if (ObjectChecker.isEmptyOrNull(user))
            return ResponseEntity.badRequest().body("user doesn't exist");
        if (ObjectChecker.isEmptyOrNull(admin))
            return ResponseEntity.badRequest().body("admin doesn't exist");
        for (UserGroupChatRooms usergroupchatrooms : admin.getUserGroupChatRooms()
        )
        {
            if (usergroupchatrooms.getChatRoom().equals(room) && usergroupchatrooms.getIsAdmin())
                if (usergroupchatrooms.getChatRoom().fetchChatRoomUsers().contains(user))
                    room.fetchChatRoomUsers().remove(user);

        }


        return ResponseEntity.ok("User: " + user.getUsername() + " was removed from room: "+room.getName() +" successfully");
    }

    /***
     * deletes  a room from the repo and removing  all useres reqiuers an admin id
     * it removes the room from all the users chatrooms database --and vise versa removes the users from the room database then deletes it
     * @param adminId id of one of the admins of the room
     * @param roomId id of the room to be deleted
     * @return ResponseEmtity<String> with the name of the room is deleted
     */
    @DeleteMapping(value = "/delete-room")
    public ResponseEntity<String> DeleteRoom(@RequestParam Long adminId, @RequestParam Long roomId)
    {
        GroupChatRoom room = groupChatRoomRepository.findById(roomId).orElse(null);
        User admin = userRepository.findById(adminId).orElse(null);
        if (ObjectChecker.isEmptyOrNull(room))
            return ResponseEntity.badRequest().body("room doesn't exist");
        if (ObjectChecker.isEmptyOrNull(admin))
            return ResponseEntity.badRequest().body("user doesn't exist");
        for (UserGroupChatRooms usergroupchatrooms : admin.getUserGroupChatRooms()
        )
        {
            if (usergroupchatrooms.getChatRoom().equals(room) && usergroupchatrooms.getIsAdmin())
                groupChatRoomRepository.delete(room);
            return ResponseEntity.ok("Room: " + room.getName() + " was deleted successfully");
        }
        return ResponseEntity.badRequest().body("u are not an admin in this room");
    }

    @GetMapping(value = "/find-room")
    public ResponseEntity<Object> FindRoomById(@RequestParam Long roomId, @RequestParam ChatRoomType type)
    {
        if (ObjectChecker.isEmptyOrNull(roomId))
            return ResponseEntity.badRequest().body("Enter valid Room ID");
        if (ObjectChecker.isEmptyOrNull(type))
            return ResponseEntity.badRequest().body("Enter room type");
        AbsChatRoom chatRoom = null;
        if (type == ChatRoomType.Group)
            chatRoom = groupChatRoomRepository.findById(roomId).orElse(null);
        else if (type == ChatRoomType.Private)
            chatRoom = privateChatRoomRepository.findById(roomId).orElse(null);
        if (chatRoom == null)
            return ResponseEntity.badRequest().body("Room was not found");
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(chatRoom);

    }
}

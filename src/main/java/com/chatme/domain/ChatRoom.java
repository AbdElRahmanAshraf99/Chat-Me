package com.chatme.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    //list of members relation: same room multi users and vise versa same member mutli rooms
    @ManyToMany(mappedBy = "chatRooms", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"chatRooms","friends"})
    private List<User> members = new ArrayList<>();
    //list of admins relation: me room multi admins and vise versa same admins mutli rooms
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_adminstrated_chat_rooms", joinColumns = @JoinColumn(name = "chat_room_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonIgnoreProperties({"chatRooms","friends"})
    private List<User> admins = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    ChatRoomType type;
}

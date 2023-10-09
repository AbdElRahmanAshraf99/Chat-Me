package com.chatme.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@Table(name = "ChatUser")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String password;
    private String email;

    @CreationTimestamp
    private LocalDateTime creationDate;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "ChatUser_friends", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "friend_id"))
    @JsonIgnoreProperties({"friends","chatRooms"})
    private List<User> friends = new ArrayList<>();

    //    @ManyToMany(cascade = CascadeType.ALL)
    //    @JoinTable(name = "ChatRoom_members", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "chatRoom_id"))
    //    @JsonIgnoreProperties({"members", "admins"})
    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties({"user"})
    private List<UserPrivateChatRooms> userPrivateChatRooms = new ArrayList<>();
    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties({"user"})
    private List<UserGroupChatRooms> userGroupChatRooms = new ArrayList<>();
}

package com.chatme.domain;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@Table(name = "ChatUser")
public class User implements UserDetails
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    @JsonIgnore
    private String password;
    private String email;
    @CreationTimestamp
    private LocalDateTime creationDate;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "ChatUser_friends", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "friend_id"))
    @JsonIgnoreProperties({"friends","chatRooms"})
    private List<User> friends = new ArrayList<>();
    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties({"user"})
    private List<UserPrivateChatRooms> userPrivateChatRooms = new ArrayList<>();
    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties({"user"})
    private List<UserGroupChatRooms> userGroupChatRooms = new ArrayList<>();

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Lob
    @Column(length = 2000)
    byte[] image;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private List<FriendRequest> friendRequests = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private List<SentFriendRequest> sentFriendRequests = new ArrayList<>();

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled()
    {
        return true;
    }
}

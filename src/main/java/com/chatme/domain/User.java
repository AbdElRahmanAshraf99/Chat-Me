package com.chatme.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Data
@Table(name = "CHAT_USER")
public class User
{
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
	@JoinTable(name = "user_friends", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "field_id"))
	@JsonIgnoreProperties({ "friends" })
	private List<User> friends = new ArrayList<>();
}

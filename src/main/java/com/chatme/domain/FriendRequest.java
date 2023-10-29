package com.chatme.domain;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class FriendRequest
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@JsonIncludeProperties({ "username", "firstname", "lastname", "image" })
	@ManyToOne
	private User fromUser;
	@CreationTimestamp
	private LocalDateTime creationDate;
	private Boolean isRead;
}

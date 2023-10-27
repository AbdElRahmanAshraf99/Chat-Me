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
	@JsonIgnore
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@JsonIncludeProperties({ "username" })
	@ManyToOne
	private User fromUser;
	@CreationTimestamp
	private LocalDateTime creationDate;
	private Boolean isRead;
}

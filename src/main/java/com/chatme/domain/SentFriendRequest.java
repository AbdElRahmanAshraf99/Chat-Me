package com.chatme.domain;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class SentFriendRequest
{
	@Id
	@JsonIgnore
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	@JsonIncludeProperties({ "username" })
	@ManyToOne
	User toUser;
}

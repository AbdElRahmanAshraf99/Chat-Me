package com.chatme.domain;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class UserGroupChatRooms extends AbsUserChatRoom
{
	@ManyToOne
	@JsonIncludeProperties({"id", "name", "description"})
	private GroupChatRoom chatRoom;
	private Boolean isAdmin;
}

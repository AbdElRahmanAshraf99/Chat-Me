package com.chatme.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserGroupChatRooms extends AbsUserChatRoom
{
	@ManyToOne
	@JsonIgnoreProperties({ "userGroupChatRooms" })
	private GroupChatRoom chatRoom;
	private Boolean isAdmin;
}

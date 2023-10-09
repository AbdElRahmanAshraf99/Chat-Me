package com.chatme.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserPrivateChatRooms extends AbsUserChatRoom
{
	@ManyToOne
	@JsonIgnoreProperties({ "userPrivateChatRooms" })
	private PrivateChatRoom chatRoom;
}

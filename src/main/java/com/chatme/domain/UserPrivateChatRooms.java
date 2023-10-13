package com.chatme.domain;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class UserPrivateChatRooms extends AbsUserChatRoom
{
	@ManyToOne
	@JsonIncludeProperties({"id"})
	private PrivateChatRoom chatRoom;
}

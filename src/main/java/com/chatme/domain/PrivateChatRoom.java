package com.chatme.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class PrivateChatRoom extends AbsChatRoom
{
	@OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
	private List<UserPrivateChatRooms> userPrivateChatRooms = new ArrayList<>();

	@Override
	public ChatRoomType getType()
	{
		return ChatRoomType.Private;
	}

	@Override
	public List<? extends AbsUserChatRoom> fetchChatRoomUsers()
	{
		return getUserPrivateChatRooms();
	}
}

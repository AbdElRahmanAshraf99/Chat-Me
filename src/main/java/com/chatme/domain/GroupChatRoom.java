package com.chatme.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class GroupChatRoom extends AbsChatRoom
{
	private String name;
	private String description;

	@OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
	private List<UserGroupChatRooms> userGroupChatRooms = new ArrayList<>();

	@Override
	public ChatRoomType getType()
	{
		return ChatRoomType.Group;
	}

	@Override
	public List<? extends AbsUserChatRoom> fetchChatRoomUsers()
	{
		return getUserGroupChatRooms();
	}
}

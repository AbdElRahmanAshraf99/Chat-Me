package com.chatme.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

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
	@JsonIgnoreProperties({"userPrivateChatRooms"})
	public List<? extends AbsUserChatRoom> fetchChatRoomUsers()
	{
		return getUserPrivateChatRooms();
	}
}

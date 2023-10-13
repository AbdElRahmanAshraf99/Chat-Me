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
    @JsonIgnoreProperties({"userGroupChatRooms"})
    public List<? extends AbsUserChatRoom> fetchChatRoomUsers()
    {
        return getUserGroupChatRooms();
    }
}

package com.chatme.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@MappedSuperclass
public abstract class AbsChatRoom
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    ChatRoomType type;

    public abstract ChatRoomType getType();

    public abstract List<? extends AbsUserChatRoom> fetchChatRoomUsers();
}

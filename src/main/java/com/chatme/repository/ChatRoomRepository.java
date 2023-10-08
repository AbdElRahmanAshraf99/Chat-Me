package com.chatme.repository;

import com.chatme.domain.ChatRoom;
import com.chatme.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChatRoomRepository extends CrudRepository<ChatRoom, Long> {

List<ChatRoom> findAllById (Long id);
}

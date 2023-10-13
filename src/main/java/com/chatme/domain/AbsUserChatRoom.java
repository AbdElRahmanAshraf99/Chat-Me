package com.chatme.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import lombok.Data;

@MappedSuperclass
@Data
public abstract class AbsUserChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;
    @ManyToOne
    @JsonIncludeProperties({"id", "username", "email"})
    private User user;
}

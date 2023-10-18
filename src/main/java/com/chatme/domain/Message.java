package com.chatme.domain;

import lombok.Data;

import java.text.DateFormat;
import java.time.LocalDateTime;

@Data
public class Message
{
    private String messageSender;
    private String messageReceaver;
    private Long roomId;
    private Object messageContent;
    private LocalDateTime sentDate;

}

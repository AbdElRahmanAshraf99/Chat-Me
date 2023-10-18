package com.chatme.controller;

import com.chatme.domain.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController
{
    private SimpMessagingTemplate simpMessagingTemplate;
    @MessageMapping("/alert")
    @SendTo("/users")
    private Message receiveAlert(@Payload Message allert)
    {
        return allert;
    }
    @MessageMapping("/message")
    public Message receiveMessage(@Payload Message message)
    {

       simpMessagingTemplate.convertAndSendToUser(message.getMessageReceaver(),"/room",message);

       return message;
    }
}

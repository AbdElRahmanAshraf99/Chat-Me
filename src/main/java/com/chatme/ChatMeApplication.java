package com.chatme;

import com.chatme.security.SecurityConfiguration;
import com.chatme.webSockets.WebSocketConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({SecurityConfiguration.class, WebSocketConfig.class})
public class ChatMeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatMeApplication.class, args);
    }

}

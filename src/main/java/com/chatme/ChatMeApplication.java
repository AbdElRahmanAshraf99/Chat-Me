package com.chatme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;

@SpringBootApplication
public class ChatMeApplication
{

	public static void main(String[] args)
	{
		SpringApplication.run(ChatMeApplication.class, args);
	}

}

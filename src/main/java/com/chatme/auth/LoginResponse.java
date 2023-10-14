package com.chatme.auth;

import lombok.*;

@Data
@AllArgsConstructor
public class LoginResponse
{
	String username;
	String token;
}

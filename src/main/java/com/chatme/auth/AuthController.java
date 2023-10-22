package com.chatme.auth;

import com.chatme.domain.User;
import com.chatme.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/chatme/auth")
public class AuthController
{
	@Autowired
	AuthenticationManager authenticationManager;
	@Autowired
	UserRepository userRepository;
	@Autowired
	JWTUtil jwtUtil;

	@PostMapping("/login")
	public ResponseEntity<?> login(@ModelAttribute LoginRequest req)
	{
		try
		{
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
			User user = userRepository.findByUsername(req.getUsername());
			String token = jwtUtil.createToken(user);
			LoginResponse res = new LoginResponse(user.getUsername(), token);
			return ResponseEntity.ok(res);
		}
		catch (BadCredentialsException e)
		{
			return ResponseEntity.badRequest().body("Invalid username or password");
		}
		catch (Exception e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}

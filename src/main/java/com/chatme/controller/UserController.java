package com.chatme.controller;

import com.chatme.Utils.ObjectChecker;
import com.chatme.domain.User;
import com.chatme.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.*;

@RestController
@RequestMapping("/user")
public class UserController
{
	@Autowired
	UserRepository userRepository;

	@PostMapping(value = "/add", consumes = { "multipart/form-data" })
	public ResponseEntity<String> addUser(@ModelAttribute User user)
	{
		String errorMsg = "";
		// Username Validation
		if (ObjectChecker.isEmptyOrNull(user.getUsername()))
			errorMsg += (ObjectChecker.isEmptyOrNull(errorMsg) ? "" : "\n") + "- Username is required";
		if (!userRepository.findAllByUsername(user.getUsername()).isEmpty())
			errorMsg += (ObjectChecker.isEmptyOrNull(errorMsg) ? "" : "\n") + "- Username (" + user.getUsername() + ") is already exists";
		// Firstname Validation
		if (ObjectChecker.isEmptyOrNull(user.getFirstname()))
			errorMsg += (ObjectChecker.isEmptyOrNull(errorMsg) ? "" : "\n") + "- Firstname is required";
		// Lastname Validation
		if (ObjectChecker.isEmptyOrNull(user.getLastname()))
			errorMsg += (ObjectChecker.isEmptyOrNull(errorMsg) ? "" : "\n") + "- Lastname is required";
		// Email Validation
		if (ObjectChecker.isNotEmptyOrNull(user.getEmail()))
		{
			if (!userRepository.findAllByEmail(user.getEmail()).isEmpty())
				errorMsg += (ObjectChecker.isEmptyOrNull(errorMsg) ? "" : "\n") + "- Email (" + user.getEmail() + ") already exists";
			if (!isValidEmail(user.getEmail()))
				errorMsg += (ObjectChecker.isEmptyOrNull(errorMsg) ? "" : "\n") + "- Email (" + user.getEmail() + ") is not valid";
		}
		// Password Validation
		if (ObjectChecker.isEmptyOrNull(user.getPassword()))
			errorMsg += (ObjectChecker.isEmptyOrNull(errorMsg) ? "" : "\n") + "- Password is required";
		if (!isValidPassword(user.getPassword()))
			errorMsg += (ObjectChecker.isEmptyOrNull(errorMsg) ? "" : "\n") + """
					- Invalid Password.
						Password Constraints are:
						- Contains digits at least one.
						- Contains a lower case letter at least one.
						- Contains a upper case letter at least one.
						- Contains a special character at least one.
						- No whitespace allowed.
						- Password length must be greater than or equal to eight characters.""";
		user.setPassword(user.getPassword());
		if (ObjectChecker.isNotEmptyOrNull(errorMsg))
			return ResponseEntity.badRequest().body(errorMsg);
		userRepository.save(user);
		return ResponseEntity.ok("Successful");
	}

	@GetMapping(value = "/get-all")
	public ResponseEntity<List<User>> getAllUsers()
	{
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body((List<User>) userRepository.findAll());
	}

	@GetMapping("/find-user")
	public ResponseEntity<List<User>> findUsers(@RequestBody String usernameOrEmail)
	{
		if (ObjectChecker.isEmptyOrNull(usernameOrEmail))
			return ResponseEntity.ok(new ArrayList<>());
		return ResponseEntity.ok(userRepository.findTop25ByUsernameContainingOrEmailContainingOrderByIdAsc(usernameOrEmail, usernameOrEmail));
	}

	@PostMapping("/add_friend")
	public ResponseEntity<String> addFriend(@RequestParam Long userId, @RequestParam Long friendId)
	{
		if (ObjectChecker.isAnyEmptyOrNull(userId, friendId))
			return ResponseEntity.badRequest().body("Can't find user with id (null)");
		User currentUser = userRepository.findById(userId).orElse(null);
		if (currentUser == null)
			return ResponseEntity.badRequest().body("Can't find user with id (" + userId + ")");
		User friend = userRepository.findById(friendId).orElse(null);
		if (friend == null)
			return ResponseEntity.badRequest().body("Can't find user with id (" + friendId + ")");
		currentUser.getFriends().add(friend);
		userRepository.save(currentUser);
		return ResponseEntity.ok("User (" + friendId + ") has been added to your friends.");
	}

	public static boolean isValidEmail(String email)
	{
		Pattern pattern = Pattern.compile(
				"^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	public static boolean isValidPassword(String password)
	{
		if (ObjectChecker.isEmptyOrNull(password))
			return false;
		//		Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
		Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$");
		Matcher matcher = pattern.matcher(password);
		return matcher.matches();
	}
}

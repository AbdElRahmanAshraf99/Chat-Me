package com.chatme.controller;

import com.chatme.domain.User;
import com.chatme.repository.UserRepository;
import com.chatme.utils.ObjectChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.regex.*;

@RestController
@RequestMapping("/user")
public class UserController
{
	@Autowired
	UserRepository userRepository;
	@Autowired
	PasswordEncoder passwordEncoder;

	@PostMapping(value = "/add", consumes = { "multipart/form-data", "application/json", MediaType.APPLICATION_FORM_URLENCODED_VALUE })
	public ResponseEntity<String> addUser(@ModelAttribute User user)
	{
		String errorMsg = "";
		// Username Validation
		if (ObjectChecker.isEmptyOrNull(user.getUsername()))
		{
			errorMsg += (ObjectChecker.isEmptyOrNull(errorMsg) ? "" : "\n") + "- Username is required";
		}
		if (ObjectChecker.isNotEmptyOrNull(userRepository.findByUsername(user.getUsername())))
		{
			return ResponseEntity.badRequest().body("Username (" + user.getUsername() + ") is already exists");
		}
		errorMsg += appendValidationMsgToMyMsg(errorMsg, validateRequiredFieldsAndAppendMsgIfNeeded("Firstname", user.getFirstname()));
		errorMsg += appendValidationMsgToMyMsg(errorMsg, validateRequiredFieldsAndAppendMsgIfNeeded("Lastname", user.getLastname()));
		errorMsg += appendValidationMsgToMyMsg(errorMsg, validateEmailAndAppendMsgIfNeeded(user.getEmail()));
		errorMsg += appendValidationMsgToMyMsg(errorMsg, validatePasswordAndAppendMsgIfNeeded(user.getPassword()));
		if (ObjectChecker.isNotEmptyOrNull(errorMsg))
			return ResponseEntity.badRequest().body(errorMsg);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
		return ResponseEntity.ok("Successful");
	}

	private String appendValidationMsgToMyMsg(String errorMsg, String validationMsg)
	{
		if (ObjectChecker.isEmptyOrNull(validationMsg))
			return "";
		return (ObjectChecker.isEmptyOrNull(errorMsg) ? "" : "\n") + validationMsg;
	}

	@GetMapping(value = "/get-all")
	public ResponseEntity<List<User>> getAllUsers()
	{
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body((List<User>) userRepository.findAll());
	}

	@GetMapping("/find-user")
	public ResponseEntity<List<User>> findUsers(@RequestParam String usernameOrEmail,Principal principal)
	{
		//TODO:: exclude me and my friends
		if (ObjectChecker.isEmptyOrNull(usernameOrEmail))
			return ResponseEntity.ok(new ArrayList<>());
		List<String>currentAndFriendsNames=new ArrayList<>();
		User user = userRepository.findByUsername(principal.getName());
		currentAndFriendsNames.add(principal.getName());
		for(User friend:user.getFriends()){
		currentAndFriendsNames.add(	friend.getUsername());
		}
		return ResponseEntity.ok(userRepository.findTop25ByUsernameContainingOrEmailContainingAndUsernameNotInOrderByUsername(usernameOrEmail, usernameOrEmail,currentAndFriendsNames));
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
		// TODO:: Check Friend not in user friends
		if(currentUser.getFriends().contains(friend)||friend.getFriends().contains(currentUser))
			return ResponseEntity.ok("User (" + friendId + ") Is already A friend.");
		currentUser.getFriends().add(friend);
		friend.getFriends().add(currentUser);
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

	@PatchMapping("/update-user")
	public ResponseEntity<String> updateUserData(@ModelAttribute User user)
	{
		User realUser = userRepository.findById(user.getId()).orElse(null);
		if (realUser == null)
			return ResponseEntity.badRequest().body("Wrong User Id");
		String errorMsg = "";
		if (ObjectChecker.isNotEmptyOrNull(user.getFirstname()))
		{
			errorMsg = appendValidationMsgToMyMsg(errorMsg, validateRequiredFieldsAndAppendMsgIfNeeded("Firstname", user.getFirstname()));
			realUser.setFirstname(user.getFirstname());
		}
		if (ObjectChecker.isNotEmptyOrNull(user.getLastname()))
		{
			errorMsg = appendValidationMsgToMyMsg(errorMsg, validateRequiredFieldsAndAppendMsgIfNeeded("Lastname", user.getLastname()));
			realUser.setLastname(user.getLastname());
		}
		if (ObjectChecker.isNotEmptyOrNull(user.getEmail()))
		{
			errorMsg = appendValidationMsgToMyMsg(errorMsg, validateEmailAndAppendMsgIfNeeded(user.getEmail()));
			realUser.setEmail(user.getEmail());
		}
		if (ObjectChecker.isNotEmptyOrNull(user.getPassword()))
		{
			errorMsg = appendValidationMsgToMyMsg(errorMsg, validatePasswordAndAppendMsgIfNeeded(user.getPassword()));
			realUser.setPassword(user.getPassword());
		}
		if (ObjectChecker.isNotEmptyOrNull(errorMsg))
		{
			return ResponseEntity.badRequest().body(errorMsg);
		}
		userRepository.save(realUser);
		return ResponseEntity.ok("Success");
	}

	private String validatePasswordAndAppendMsgIfNeeded(String value)
	{
		String errorMsg = "";
		errorMsg += (ObjectChecker.isEmptyOrNull(errorMsg) ? "" : "\n") + validateRequiredFieldsAndAppendMsgIfNeeded("Password", value);
		if (!isValidPassword(value))
			errorMsg += (ObjectChecker.isEmptyOrNull(errorMsg) ? "" : "\n") + """
					- Invalid Password.
						Password Constraints are:
						- Contains digits at least one.
						- Contains a lower case letter at least one.
						- Contains a upper case letter at least one.
						- Contains a special character at least one.
						- No whitespace allowed.
						- Password length must be greater than or equal to eight characters.""";
		return errorMsg;
	}

	private String validateEmailAndAppendMsgIfNeeded(String value)
	{
		if (ObjectChecker.isEmptyOrNull(value))
			return "";
		String errorMsg = "";
		if (!userRepository.findAllByEmail(value).isEmpty())
			errorMsg += (ObjectChecker.isEmptyOrNull(errorMsg) ? "" : "\n") + "- Email (" + value + ") already exists";
		if (!isValidEmail(value))
			errorMsg += (ObjectChecker.isEmptyOrNull(errorMsg) ? "" : "\n") + "- Email (" + value + ") is not valid";
		return errorMsg;
	}

	public static String validateRequiredFieldsAndAppendMsgIfNeeded(String fieldName, String fieldValue)
	{
		if (ObjectChecker.isEmptyOrNull(fieldValue))
			return "- " + fieldName + " is required";
		return "";
	}

	@DeleteMapping("delete-user")
	public ResponseEntity<String> deleteUser(@RequestParam Long id)
	{
		if (userRepository.findById(id).isEmpty())
			return ResponseEntity.badRequest().body("User not found");
		userRepository.deleteById(id);
		return ResponseEntity.ok("Success");
	}

	@DeleteMapping("delete-all-user")
	public ResponseEntity<String> deleteAllUsers()
	{
		userRepository.deleteAll();
		return ResponseEntity.ok("Success");
	}

	@GetMapping("/fetchUserData")
	public ResponseEntity<User> fetchUserData(Principal principal)
	{
		User user = userRepository.findByUsername(principal.getName());
		return ResponseEntity.ok(user);
	}
}

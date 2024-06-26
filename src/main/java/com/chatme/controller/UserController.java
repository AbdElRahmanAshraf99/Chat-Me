package com.chatme.controller;

import com.chatme.domain.*;
import com.chatme.repository.UserRepository;
import com.chatme.utils.ObjectChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

	/***
	 * Creates  a new User entity in the data base
	 * @param user A hasj map or <String,String> containing the user data
	 * @return ResponseEntity<String> containing the Message of Success or failure
	 */
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
		return ResponseEntity.ok("Register Successful");
	}

	/***
	 *  Appends a New String to an old String
	 * @param errorMsg  String oldString to beappended to
	 * @param validationMsg String new String to append
	 * @return new Message consists of error Msg +""+validationMsg
	 */
	private String appendValidationMsgToMyMsg(String errorMsg, String validationMsg)
	{
		if (ObjectChecker.isEmptyOrNull(validationMsg))
			return "";
		return (ObjectChecker.isEmptyOrNull(errorMsg) ? "" : "\n") + validationMsg;
	}

	/***
	 * GetMapping that Returns all theUsers in the DataBase
	 * @return LIst<USer> containing all the users in DataBase
	 */
	@GetMapping(value = "/get-all")
	public ResponseEntity<List<User>> getAllUsers()
	{
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body((List<User>) userRepository.findAll());
	}

	/***
	 * etMapping that Returns user By their USername or email from the DataBase
	 * @param usernameOrEmail String userName or email
	 * @param principal Princibale Headr :Authorization containing a JWt that hase the data of the user sending the Request
	 * @return List<USer>of Top 25 Resault Found in the DataBase Orderd By id ASc
	 */
	@GetMapping("/find-user")
	public ResponseEntity<List<User>> findUsers(@RequestParam String usernameOrEmail,Principal principal)
	{
		if (ObjectChecker.isEmptyOrNull(usernameOrEmail))
			return ResponseEntity.ok(new ArrayList<>());
		User user = userRepository.findByUsername(principal.getName());
		List<User> matchedUsers = userRepository.findTop25ByUsernameContainingOrEmailContainingOrderByIdAsc(usernameOrEmail, usernameOrEmail);
		matchedUsers.removeAll(user.getFriends());
		matchedUsers.remove(user);
		return ResponseEntity.ok(matchedUsers);
	}

	/***
	 * Checks whether an email is on the valid Form (examole.123@example.com)
	 * @param email String email
	 * @return Boolean <True> the email Form is valid</True> <False> the email form is onvalid</False>
	 */
	public static boolean isValidEmail(String email)
	{
		Pattern pattern = Pattern.compile(
				"^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	/***
	 * Checks whether a password is valid containing uppercase and lower case and numbers and special characters and is over 8 characters long
	 * @param password String password
	 * @return Boolean <True>the password form is valid</True> <False>the password form is invalid</False>
	 */
	public static boolean isValidPassword(String password)
	{
		if (ObjectChecker.isEmptyOrNull(password))
			return false;
		//		Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
		Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$");
		Matcher matcher = pattern.matcher(password);
		return matcher.matches();
	}

	/***
	 * Updates the Data Sent to the Server of the user
	 * @param principal Princibale Headr :Authorization containing a JWt that hase the data of the user sending the Request
	 * @param user Map<String,String> containing the attributes to be updated in user
	 * @return <String> on un successful Request containing reason for failure</String> <User>the updated user after the success of the request</User>
	 */
	@PatchMapping(value = "/update-user", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity updateUserData(Principal principal, @RequestParam Map<String, String> user)
	{
		User realUser = userRepository.findByUsername(principal.getName());
		if (realUser == null)
			return ResponseEntity.badRequest().body("Wrong Username");
		String errorMsg = "";
		if (user.containsKey("firstname"))
		{
			errorMsg = appendValidationMsgToMyMsg(errorMsg, validateRequiredFieldsAndAppendMsgIfNeeded("Firstname", user.get("firstname")));
			realUser.setFirstname(user.get("firstname"));
		}
		if (user.containsKey("lastname"))
		{
			errorMsg = appendValidationMsgToMyMsg(errorMsg, validateRequiredFieldsAndAppendMsgIfNeeded("Lastname", user.get("lastname")));
			realUser.setLastname(user.get("lastname"));
		}
		if (user.containsKey("email"))
		{
			errorMsg = appendValidationMsgToMyMsg(errorMsg, validateEmailAndAppendMsgIfNeeded(user.get("email")));
			realUser.setEmail(user.get("email"));
		}
		if (ObjectChecker.isNotEmptyOrNull(errorMsg))
		{
			return ResponseEntity.badRequest().body(errorMsg);
		}
		userRepository.save(realUser);
		return ResponseEntity.ok(realUser);
	}

	/***
	 * PostMapping for user to change passwords old password have to match the database password
	 * @param principal Princibale Headr :Authorization containing a JWt that hase the data of the user sending the Request
	 * @param oldPassword <String> the old password to be changed</String>
	 * @param newPassword <String>new passwod to be changed to</String>
	 * @return RsponseEntity<String>containing the success msg or failure message</STring>
	 */
	@PatchMapping(value = "/changePassword", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity changeUserPassword(Principal principal, @RequestParam String oldPassword, @RequestParam String newPassword)
	{
		User user = userRepository.findByUsername(principal.getName());
		if (user == null)
			return ResponseEntity.badRequest().body("Wrong Username");
		if (ObjectChecker.isAnyEmptyOrNull(oldPassword, newPassword))
			return ResponseEntity.badRequest().body("Wrong Data");
		if (!passwordEncoder.matches(oldPassword, user.getPassword()))
			return ResponseEntity.badRequest().body("Old Password is not Correct");
		String errorMsg = "";
		errorMsg = appendValidationMsgToMyMsg(errorMsg, validatePasswordAndAppendMsgIfNeeded(newPassword));
		if (ObjectChecker.isNotEmptyOrNull(errorMsg))
			return ResponseEntity.badRequest().body(errorMsg);
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
		return ResponseEntity.ok("Password Changed Successfully");
	}

	/***
	 * Validates the passwordm to be present or empty or null
	 * @param value String password to be validated
	 * @return String error msg if the password is not valid
	 */
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

	/***
	 * Validates the email to be present or empty or null
	 * @param value String email to be validated
	 * @return String error msg if the email is not valid
	 */
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

	/***
	 * Validates any Field to be present or empty or null
	 * @param fieldName String Field name to be validated
	 * @param fieldValue String Field Value to be validated
	 * @return String error msg if the Field is not valid
	 */
	public static String validateRequiredFieldsAndAppendMsgIfNeeded(String fieldName, String fieldValue)
	{
		if (ObjectChecker.isEmptyOrNull(fieldValue))
			return "- " + fieldName + " is required";
		return "";
	}

	/***
	 * DeleteMapping that delets a user entity in the database
	 * @param principal Princibale Headr :Authorization containing a JWt that hase the data of the user sending the Request
	 * @param password <String> the password of the current user that want to delete his data to be </String>
	 * @return ResponseEntity<String> containing the success or failure msg</String>
	 */
	@DeleteMapping("/deleteAccount")
	public ResponseEntity<String> deleteUser(Principal principal,@RequestParam String password)
	{
		User user = userRepository.findByUsername(principal.getName());
		if (!passwordEncoder.matches(password, user.getPassword()))
			return ResponseEntity.badRequest().body("Password is not Correct");
		userRepository.deleteById(user.getId());
		return ResponseEntity.ok("Account deleted Successfully");
	}

	/***
	 * Deletes all the User database
	 * @return <STring> containing the success message</STring>
	 */
	@DeleteMapping("delete-all-user")
	public ResponseEntity<String> deleteAllUsers()
	{
		userRepository.deleteAll();
		return ResponseEntity.ok("Success");
	}

	/***
	 * GetMapping fetches the data of the current user sending the request
	 * @param principal Princibale Headr :Authorization containing a JWt that hase the data of the user sending the Request
	 * @return <USER> data of the user sending the request</USER>
	 */
	@GetMapping("/fetchUserData")
	public ResponseEntity<User> fetchUserData(Principal principal)
	{
		User user = userRepository.findByUsername(principal.getName());
		return ResponseEntity.ok(user);
	}

	/***
	 * PostMapping that Uploads a profile picture to the user sending the request
	 * @param image <MultipartFile> containing the ByteData of the image file uploaded</MultipartFile>
	 * @param principal Princibale Headr :Authorization containing a JWt that hase the data of the user sending the Request
	 * @return ResponseEntity<String> containig the Success message of failure or success</String>
	 */
	@PostMapping("/saveImage")
	public ResponseEntity<String> addImage(@RequestParam("image") MultipartFile image, Principal principal)
	{
		User currentUser = userRepository.findByUsername(principal.getName());
		if (image == null || image.getContentType() == null || !image.getContentType().startsWith("image/"))
			return ResponseEntity.badRequest().body("Attachment is not an image file");
		try
		{
			currentUser.setImage(image.getBytes());
			userRepository.save(currentUser);
			return ResponseEntity.ok("profile Image changed successfully");
		}
		catch (IOException e)
		{
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	/***
	 * PostMapping that removes the Blob of the image from the database for the user sending the request
	 * @param principal Princibale Headr :Authorization containing a JWt that hase the data of the user sending the Request
	 * @return ResponseEntity<String> containig the Success message of failure or success</String>
	 */
	@PostMapping("/removeImage")
	public ResponseEntity<String> removeImage(Principal principal)
	{
		User currentUser = userRepository.findByUsername(principal.getName());
		currentUser.setImage(null);
		userRepository.save(currentUser);
		return ResponseEntity.ok("profile Image removed successfully");
	}

	/***
	 * GetMapping that fetches the blob data of the profile image from the database for the user sending the request
	 * @param principal Princibale Headr :Authorization containing a JWt that hase the data of the user sending the Request
	 * @return ResponseEntity<byte[]> containig the image File as Byte[]</byte[]>
	 */
	@GetMapping("/getImage")
	public ResponseEntity getImage(Principal principal)
	{
		User currentUser = userRepository.findByUsername(principal.getName());
		byte[] image = currentUser.getImage();
		return ResponseEntity.ok().body(image);
	}

	@PostMapping("/sendFriendRequest")
	public ResponseEntity sendFriendRequest(Principal principal, @RequestParam String friendUsername)
	{
		User currentUser = userRepository.findByUsername(principal.getName());
		User friend = userRepository.findByUsername(friendUsername);
		FriendRequest request = friend.getFriendRequests().stream()
				.filter(l -> ObjectChecker.areEqual(l.getFromUser().getUsername(), currentUser.getUsername())).findFirst().orElse(null);
		if (request != null)
			return ResponseEntity.ok("Friend Request Sent Successfully");
		FriendRequest friendRequest = new FriendRequest();
		friendRequest.setFromUser(currentUser);
		friend.getFriendRequests().add(friendRequest);
		SentFriendRequest sentFriendRequest = new SentFriendRequest();
		sentFriendRequest.setToUser(friend);
		currentUser.getSentFriendRequests().add(sentFriendRequest);
		userRepository.saveAll(List.of(currentUser, friend));
		return ResponseEntity.ok("Friend Request Sent Successfully");
	}

	@PostMapping("/cancelFriendRequest")
	public ResponseEntity cancelFriendRequest(Principal principal, @RequestParam String friendUsername)
	{
		User user = userRepository.findByUsername(principal.getName());
		User friend = userRepository.findByUsername(friendUsername);
		FriendRequest request = friend.getFriendRequests().stream()
				.filter(l -> ObjectChecker.areEqual(l.getFromUser().getUsername(), user.getUsername())).findFirst().orElse(null);
		SentFriendRequest sentFriendRequest = user.getSentFriendRequests().stream()
				.filter(l -> ObjectChecker.areEqual(l.getToUser().getUsername(), friend.getUsername())).findFirst().orElse(null);
		if (request == null && sentFriendRequest == null)
			return ResponseEntity.ok("Friend Request Removed Successfully");
		friend.getFriendRequests().remove(request);
		user.getSentFriendRequests().remove(sentFriendRequest);
		userRepository.saveAll(List.of(user, friend));
		return ResponseEntity.ok("Friend Request Removed Successfully");
	}

	@PostMapping("/acceptFriendRequest")
	public ResponseEntity<String> acceptFriendRequest(Principal principal, @RequestParam String friendUsername)
	{
		if (ObjectChecker.isEmptyOrNull(friendUsername))
			return ResponseEntity.badRequest().body("Can't find user with username (" + friendUsername + ")");
		User currentUser = userRepository.findByUsername(principal.getName());
		User friend = userRepository.findByUsername(friendUsername);
		if (currentUser.getFriendRequests().stream().anyMatch(l -> ObjectChecker.areEqual(l.getFromUser(), friend)))
			currentUser.getFriendRequests().removeIf(l -> ObjectChecker.areEqual(l.getFromUser(), friend));
		if (friend.getSentFriendRequests().stream().anyMatch(l -> ObjectChecker.areEqual(l.getToUser(), currentUser)))
			friend.getSentFriendRequests().removeIf(l -> ObjectChecker.areEqual(l.getToUser(), currentUser));
		currentUser.getFriends().add(friend);
		friend.getFriends().add(currentUser);
		userRepository.saveAll(List.of(friend, currentUser));
		return ResponseEntity.ok("User (" + friend.getFirstname() + " " + friend.getLastname() + ") has been added to your friends.");
	}

	@PostMapping("/declineFriendRequest")
	public ResponseEntity<String> declineFriendRequest(Principal principal, @RequestParam String friendUsername)
	{
		if (ObjectChecker.isEmptyOrNull(friendUsername))
			return ResponseEntity.badRequest().body("Can't find user with username (" + friendUsername + ")");
		User currentUser = userRepository.findByUsername(principal.getName());
		User friend = userRepository.findByUsername(friendUsername);
		if (currentUser.getFriendRequests().stream().anyMatch(l -> ObjectChecker.areEqual(l.getFromUser(), friend)))
			currentUser.getFriendRequests().removeIf(l -> ObjectChecker.areEqual(l.getFromUser(), friend));
		if (friend.getSentFriendRequests().stream().anyMatch(l -> ObjectChecker.areEqual(l.getToUser(), currentUser)))
			friend.getSentFriendRequests().removeIf(l -> ObjectChecker.areEqual(l.getToUser(), currentUser));
		userRepository.saveAll(List.of(friend, currentUser));
		return ResponseEntity.ok("Friend Request is successfully declined.");
	}

	@GetMapping("/readFriendRequests")
	public ResponseEntity readFriendRequests(Principal principal)
	{
		User user = userRepository.findByUsername(principal.getName());
		return ResponseEntity.ok(user.getFriendRequests());
	}
}

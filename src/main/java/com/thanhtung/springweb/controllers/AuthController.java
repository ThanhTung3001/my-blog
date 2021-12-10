package com.thanhtung.springweb.controllers;

import java.util.List;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.validation.Valid;

import com.thanhtung.springweb.Entity.UserEntity;
import com.thanhtung.springweb.security.services.UserService;
import com.thanhtung.springweb.payload.request.LoginRequest;
import com.thanhtung.springweb.payload.request.SignupRequest;
import com.thanhtung.springweb.payload.response.JwtResponse;
import com.thanhtung.springweb.payload.response.MessageResponse;
import com.thanhtung.springweb.repository.RoleRepository;
import com.thanhtung.springweb.repository.UserRepository;
import com.thanhtung.springweb.security.jwt.JwtUtils;
import com.thanhtung.springweb.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	UserService userService;
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt,
												 userDetails.getId(), 
												 userDetails.getUsername(), 
												 userDetails.getEmail(), 
												 roles));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) throws MessagingException {

         switch (userService.Save(signUpRequest)){
			 case 0:
				 return ResponseEntity
				.badRequest()
        		.body(new MessageResponse("Error: Username is already taken!"));
			 case 1:
				 return ResponseEntity
     				.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
			 case 2:
				 return ResponseEntity.ok(new MessageResponse("UserEntity registered successfully!"));
		 }

		return ResponseEntity.ok(new MessageResponse("UserEntity registered successfully!"));
	}
	@GetMapping("/Authentication")
	public ResponseEntity<?>accountActive(@RequestParam("token")String token){
		return userService.AuthenticationActive(token)?ResponseEntity.ok(new MessageResponse("Authentication success")):ResponseEntity.badRequest().body(new MessageResponse("Authentication fail"));
	}
	@GetMapping("/forget/password")
	public ResponseEntity<?>forgotPassword(@RequestParam("token")String token){
		return userService.forgetPassword(token)?ResponseEntity.ok(new MessageResponse(token)):ResponseEntity.badRequest().body(new MessageResponse("Fail"));
	}
	@PostMapping("/forgot-password")
	public ResponseEntity<?>fgPassword(@RequestBody SignupRequest signupRequest){
		try {
			return 	userService.senderMail(signupRequest.getEmail())?ResponseEntity.ok(new MessageResponse("Check Your Email")):ResponseEntity.badRequest().body(new MessageResponse("Email not found"));
		} catch (MessagingException e) {
			return ResponseEntity.badRequest().body(new MessageResponse("have error"));
		}
	}
	@PostMapping("/change-password")
	public ResponseEntity<?>changePassword(@RequestBody UserEntity userEntity, @RequestParam("token") String token){
		userEntity.setForgetPassword(token);
	 boolean isSuccess=userService.changerPassword(userEntity);
	 return isSuccess?ResponseEntity.ok(new MessageResponse("Change Success")):ResponseEntity.badRequest().body(new MessageResponse("Change fail"));
	}
}

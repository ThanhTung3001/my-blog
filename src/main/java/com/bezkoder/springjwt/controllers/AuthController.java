package com.bezkoder.springjwt.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.validation.Valid;

import com.bezkoder.springjwt.Entity.RoleEntity;
import com.bezkoder.springjwt.Entity.UserEntity;
import com.bezkoder.springjwt.security.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.bezkoder.springjwt.Entity.ERole;
import com.bezkoder.springjwt.payload.request.LoginRequest;
import com.bezkoder.springjwt.payload.request.SignupRequest;
import com.bezkoder.springjwt.payload.response.JwtResponse;
import com.bezkoder.springjwt.payload.response.MessageResponse;
import com.bezkoder.springjwt.repository.RoleRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import com.bezkoder.springjwt.security.jwt.JwtUtils;
import com.bezkoder.springjwt.security.services.UserDetailsImpl;

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
	public ResponseEntity<?>changePassword(@RequestBody UserEntity userEntity,@RequestParam("token") String token){
		userEntity.setForgetPassword(token);
	 boolean isSuccess=userService.changerPassword(userEntity);
	 return isSuccess?ResponseEntity.ok(new MessageResponse("Change Success")):ResponseEntity.badRequest().body(new MessageResponse("Change fail"));
	}
}

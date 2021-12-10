package com.thanhtung.springweb.controllers;

import com.thanhtung.springweb.Entity.RoleEntity;
import com.thanhtung.springweb.security.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
	@Autowired
public RoleService roleService ;
	@GetMapping("/all")
	public String allAccess() {
		return "Public Content.";
	}
	
	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public String userAccess() {
		return "UserEntity Content.";
	}

	@GetMapping("/mod")
	@PreAuthorize("hasRole('MODERATOR')")
	public String moderatorAccess() {
		return "Moderator Board.";
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminAccess() {
		return "Admin Board.";
	}

	@PostMapping("Role")
	public ResponseEntity<?>addRole(@RequestBody List<RoleEntity> roles){
		List<RoleEntity>roleResponse= new ArrayList<>();
		try{
			roles.forEach(e->{
				roleResponse.add(roleService.Save(e));
			});
		}catch (Exception ex){
             return  ResponseEntity.badRequest().body(roleResponse);
		}
		return ResponseEntity.ok(roleResponse);
	}
}

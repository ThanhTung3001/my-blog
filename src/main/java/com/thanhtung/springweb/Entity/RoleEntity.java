package com.thanhtung.springweb.Entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "roles")
@Setter
@Getter
public class RoleEntity extends AbstractEntity{

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private ERole name;



}
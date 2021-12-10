package com.bezkoder.springjwt.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bezkoder.springjwt.Entity.ERole;
import com.bezkoder.springjwt.Entity.RoleEntity;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {
	Optional<RoleEntity> findByName(ERole name);
}

package com.thanhtung.springweb.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.thanhtung.springweb.Entity.ERole;
import com.thanhtung.springweb.Entity.RoleEntity;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {
	Optional<RoleEntity> findByName(ERole name);
}

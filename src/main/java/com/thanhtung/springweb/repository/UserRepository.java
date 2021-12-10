package com.thanhtung.springweb.repository;

import java.util.Optional;
import java.util.UUID;

import com.thanhtung.springweb.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
	Optional<UserEntity> findByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

	Boolean existsByUuidAuthen(String uuid);

	UserEntity findAllByUuidAuthen(String uuid);

	Boolean existsByForgetPassword(String token);

	UserEntity findAllByEmail(String mail);

	UserEntity findAllByForgetPassword(String token);
}

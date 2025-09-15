package com.LSM.smboard.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<SiteUser, Long>{

	 public Optional<SiteUser> findByUsername(String username); //유저아이디로 조회
}

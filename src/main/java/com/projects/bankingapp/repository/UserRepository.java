package com.projects.bankingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.bankingapp.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
  Boolean existsByEmail(String email);

  Boolean existsByAccountNumber(String accountNumber);

  User findByAccountNumber(String accountNumber);
}

package com.projects.bankingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.projects.bankingapp.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
  
}

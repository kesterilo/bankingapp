package com.projects.bankingapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projects.bankingapp.dto.TransactionDto;
import com.projects.bankingapp.entity.Transaction;
import com.projects.bankingapp.repository.TransactionRepository;

@Service
public class TransactionServiceImpl implements TransactionService {
  
  @Autowired
  TransactionRepository transactionRepository;

  @Override
  public void saveTransaction(TransactionDto transactionDto) {
    Transaction transaction = Transaction.builder()
        .transactionType(transactionDto.getTransactionType())
        .accountNumber(transactionDto.getAccountNumber())
        .amount(transactionDto.getAmount())
        .status("SUCCESS")
        .build();
        
    transactionRepository.save(transaction);
    System.out.println("Transaction saved successfully");
  }
  
}

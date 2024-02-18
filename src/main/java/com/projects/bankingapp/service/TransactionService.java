package com.projects.bankingapp.service;

import com.projects.bankingapp.dto.TransactionDto;

public interface TransactionService {
  void saveTransaction(TransactionDto transactionDto);
}

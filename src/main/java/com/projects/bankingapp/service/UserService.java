package com.projects.bankingapp.service;

import com.projects.bankingapp.dto.BankResponse;
import com.projects.bankingapp.dto.CreditDebitRequest;
import com.projects.bankingapp.dto.EnquiryRequest;
import com.projects.bankingapp.dto.UserInfo;

public interface UserService {
  BankResponse createAccount(UserInfo userRequest);

  BankResponse balanceEnquiry(EnquiryRequest request);
  
  String nameEnquiry(EnquiryRequest request);

  BankResponse creditAccount(CreditDebitRequest request);

  BankResponse debitAccount(CreditDebitRequest request);
}

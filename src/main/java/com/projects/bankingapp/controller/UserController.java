package com.projects.bankingapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.projects.bankingapp.dto.BankResponse;
import com.projects.bankingapp.dto.CreditDebitRequest;
import com.projects.bankingapp.dto.EnquiryRequest;
import com.projects.bankingapp.dto.TransferRequest;
import com.projects.bankingapp.dto.UserInfo;
import com.projects.bankingapp.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
  
  @Autowired
  UserService userService;
  
  @PostMapping
  public BankResponse createAccount(@RequestBody UserInfo userInfo) {
    return userService.createAccount(userInfo);
  }
  
  @GetMapping("/balanceEnquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest request) {
        return userService.balanceEnquiry(request);
    }
    
  @GetMapping("/nameEnquiry")
  public String nameEnquiry(@RequestBody EnquiryRequest request) {
    return userService.nameEnquiry(request);
  }
  
  @PostMapping("/credit")
  public BankResponse creditAccount(@RequestBody CreditDebitRequest request) {
    return userService.creditAccount(request);
  }
  
  @PostMapping("/debit")
  public BankResponse debitAccount(@RequestBody CreditDebitRequest request) {
    return userService.debitAccount(request);
  }
  
  @PostMapping("/transfer")
  public BankResponse transger(@RequestBody TransferRequest request) {
    return userService.transfer(request);
  }
}

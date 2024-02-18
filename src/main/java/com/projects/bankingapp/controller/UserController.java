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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Account Management APIs")
public class UserController {
  
  @Autowired
  UserService userService;
  
  @Operation(
    summary = "Create new User Account",
    description = "Creating a new user and assigning an account ID"
  )
  @ApiResponse(
    responseCode = "201",
    description = "Http Status: 201 CREATED"
  )
  @PostMapping
  public BankResponse createAccount(@RequestBody UserInfo userInfo) {
    return userService.createAccount(userInfo);
  }
  
  @Operation(
    summary = "Balance Enquiry",
    description = "Check the balance of a user given the user's account number"
  )
  @ApiResponse(
    responseCode = "200",
    description = "Http Status: 201 SUCCESS"
  )
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

package com.projects.bankingapp.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projects.bankingapp.config.JwtTokenProvider;
import com.projects.bankingapp.dto.AccountInfo;
import com.projects.bankingapp.dto.BankResponse;
import com.projects.bankingapp.dto.CreditDebitRequest;
import com.projects.bankingapp.dto.EmailDetails;
import com.projects.bankingapp.dto.EnquiryRequest;
import com.projects.bankingapp.dto.LoginDto;
import com.projects.bankingapp.dto.TransactionDto;
import com.projects.bankingapp.dto.TransferRequest;
import com.projects.bankingapp.dto.UserInfo;
import com.projects.bankingapp.entity.Role;
import com.projects.bankingapp.entity.User;
import com.projects.bankingapp.repository.UserRepository;
import com.projects.bankingapp.utils.AccountUtils;

@Service
public class UserServiceImpl implements UserService {
  
  @Autowired
  UserRepository userRepository;
  
  @Autowired
  EmailService emailService;
  
  @Autowired
  TransactionService transactionService;
  
  @Autowired
  PasswordEncoder passwordEncoder;
  
  @Autowired
  AuthenticationManager authenticationManager;
  
  @Autowired
  JwtTokenProvider jwtTokenProvider;

  @Override
  public BankResponse createAccount(UserInfo userInfo) {
    /*
     * creating an account - saving a new user into the db
     * check if user already has an account
     */

    if (userRepository.existsByEmail(userInfo.getEmail())) {
      BankResponse response = BankResponse.builder()
          .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
          .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
          .accountInfo(null)
          .build();
      return response;
    }

    User newUser = User.builder()
        .firstName(userInfo.getFirstName())
        .lastName(userInfo.getLastName())
        .otherName(userInfo.getOtherName())
        .gender(userInfo.getGender())
        .address(userInfo.getAddress())
        .stateOfOrigin(userInfo.getStateOfOrigin())
        .accountNumber(AccountUtils.generateAccountNumber())
        .accountBalance(BigDecimal.ZERO)
        .email(userInfo.getEmail())
        .password(passwordEncoder.encode(userInfo.getPassword()))
        .phoneNumber(userInfo.getPhoneNumber())
        .alternativePhoneNumber(userInfo.getAlternativePhoneNumber())
        .status("ACTIVE")
        .role(userInfo.getRole())
        .build();

    User savedUser = userRepository.save(newUser);
    EmailDetails emailDetails = EmailDetails.builder()
        .recipient(savedUser.getEmail())
        .subject("ACCOUNT CREATION")
        .messageBody(
            "Congratulations! Your Account has been successfully created.\nYour Account Details: \nAccount Name: "
                + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName() +
                "\nAccount Number: " + savedUser.getAccountNumber())
        .build();
    emailService.sendEmailAlert(emailDetails);
    return BankResponse.builder()
        .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
        .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
        .accountInfo(AccountInfo.builder()
            .accountBalance(savedUser.getAccountBalance())
            .accountNumber(savedUser.getAccountNumber())
            .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName())
            .build())
        .build();
  }
  
  public BankResponse login(LoginDto loginDto) {
    Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
    EmailDetails loginAlert = EmailDetails.builder()
        .subject("Your Account has been logged into")
        .recipient(loginDto.getEmail())
        .messageBody("You logged into your Account. If you did initiate this transaction please contact your Bank immediately.")
        .build();
        
    emailService.sendEmailAlert(loginAlert);
    return BankResponse.builder()    
        .responseCode("Login Success")
        .responseMessage(jwtTokenProvider.generateToken(authentication))
        .build();
  }

  @Override
  public BankResponse balanceEnquiry(EnquiryRequest request) {
    // Check if the provided account number exists in the db
    boolean userAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
    if (!userAccountExists) {
      return BankResponse.builder()
          .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
          .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
          .accountInfo(null)
          .build();
    }
    
    User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
    return BankResponse.builder()
        .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
        .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
        .accountInfo(AccountInfo.builder()
            .accountBalance(foundUser.getAccountBalance())
            .accountNumber(request.getAccountNumber())
            .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
            .build())
        .build();    
  }

  @Override
  public String nameEnquiry(EnquiryRequest request) {
    boolean userAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
    if (!userAccountExists) {
      return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
    }
    
    User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
    return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName();
  }

  @Override
  public BankResponse creditAccount(CreditDebitRequest request) {
    Boolean userAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
    if (!userAccountExists) {
      return BankResponse.builder()
          .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
          .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
          .accountInfo(null)
          .build();
    }
    
    User user = userRepository.findByAccountNumber(request.getAccountNumber());
    user.setAccountBalance(user.getAccountBalance().add(request.getAmount()));
    userRepository.save(user);
    
    //Save transaction    
    TransactionDto transactionDto = TransactionDto.builder()
        .accountNumber(user.getAccountNumber())
        .transactionType("CREDIT")
        .amount(request.getAmount())
        .build();
        
    transactionService.saveTransaction(transactionDto);    
    
    return BankResponse.builder()
        .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
        .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
        .accountInfo(AccountInfo.builder()
            .accountName(user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName())
            .accountBalance(user.getAccountBalance())
            .accountNumber(user.getAccountNumber())
            .build())
        .build();
  }

  @Override
  public BankResponse debitAccount(CreditDebitRequest request) {
    Boolean userAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());
    if (!userAccountExists) {
      return BankResponse.builder()
          .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
          .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
          .accountInfo(null)
          .build();
    }

    User user = userRepository.findByAccountNumber(request.getAccountNumber());
    if (user.getAccountBalance().compareTo(request.getAmount()) < 0) {
      return BankResponse.builder()
          .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
          .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
          .accountInfo(null)
          .build();
    }
    else {
      user.setAccountBalance(user.getAccountBalance().subtract(request.getAmount()));
      userRepository.save(user);
      //Save transaction    
      TransactionDto transactionDto = TransactionDto.builder()
        .accountNumber(user.getAccountNumber())
        .transactionType("DEBIT")
        .amount(request.getAmount())
        .build();
      transactionService.saveTransaction(transactionDto);
      return BankResponse.builder()
          .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS)
          .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
          .accountInfo(AccountInfo.builder()
              .accountNumber(request.getAccountNumber())
              .accountName(user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName())
              .accountBalance(user.getAccountBalance())
              .build())
          .build();
    }
  }

  @Override
  public BankResponse transfer(TransferRequest request) {
    boolean destinationAccountExists = userRepository.existsByAccountNumber(request.getDestinationNumber());
    if (!destinationAccountExists) {
      return BankResponse.builder()
          .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
          .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
          .accountInfo(null)
          .build();
    }
    
    User user = userRepository.findByAccountNumber(request.getSourceAccountNumber());
    if (user.getAccountBalance().compareTo(request.getAmount()) < 0) {
      return BankResponse.builder()
          .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
          .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
          .accountInfo(null)
          .build();
    }
    
    user.setAccountBalance(user.getAccountBalance().subtract(request.getAmount()));
    userRepository.save(user);
    
    User receivingUser = userRepository.findByAccountNumber(request.getDestinationNumber());
    receivingUser.setAccountBalance(receivingUser.getAccountBalance().add(request.getAmount()));
    userRepository.save(receivingUser);
    
    EmailDetails debitAlert = EmailDetails.builder()
        .subject("DEBIT ALERT")
        .recipient(user.getEmail())
        .messageBody("You sent the sum of N" + request.getAmount() + " to " + receivingUser.getFirstName() + " " + receivingUser.getLastName() + " " + receivingUser.getOtherName() + ".\n" + "The sum of N" + request.getAmount() + " has been deducted from your account.\nYour current balance is N" + user.getAccountBalance())
        .build();
    emailService.sendEmailAlert(debitAlert);
    
    EmailDetails creditAlert = EmailDetails.builder()
        .subject("CREDIT ALERT")
        .recipient(receivingUser.getEmail())
        .messageBody("You have received the sum of N" + request.getAmount() + " from " + user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName() + ". \n" + "Your current balance is N" + receivingUser.getAccountBalance())
        .build();
    emailService.sendEmailAlert(creditAlert);
    
    TransactionDto transactionDto = TransactionDto.builder()
        .accountNumber(user.getAccountNumber())
        .transactionType("DEBIT")
        .amount(request.getAmount())
        .build();
        
    transactionService.saveTransaction(transactionDto);    
    
    return BankResponse.builder()
        .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
        .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
        .accountInfo(null)
        .build();
  }
  
}

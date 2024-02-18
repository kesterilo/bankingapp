package com.projects.bankingapp.controller;

import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.DocumentException;
import com.projects.bankingapp.entity.Transaction;
import com.projects.bankingapp.service.BankStatement;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/bankStatement")
@AllArgsConstructor
public class TransactionController {
  
  private BankStatement bankStatement;
  
  public List<Transaction> generateStatement(@RequestParam String accountNumber,
                                            @RequestParam String startDate, 
                                            @RequestParam String endDate) throws FileNotFoundException, DocumentException {
    return bankStatement.generateStatement(accountNumber, startDate, endDate);
  }
}

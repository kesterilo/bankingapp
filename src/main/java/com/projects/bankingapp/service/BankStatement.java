package com.projects.bankingapp.service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.projects.bankingapp.dto.EmailDetails;
import com.projects.bankingapp.entity.Transaction;
import com.projects.bankingapp.entity.User;
import com.projects.bankingapp.repository.TransactionRepository;
import com.projects.bankingapp.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class BankStatement {
  
  private TransactionRepository transactionRepository;
  private UserRepository userRepository;
  private EmailService emailService;
  
  private static final String FILE = "/home/kester/info-tech/temp/bankingapp/documents/bankStatement.pdf";
  
  // A nested class for the purpose of designing the bank statement
  private class StatementDesigner {

    public void designStatement(List<Transaction> transactions, String accountNumber, String startDate, String endDate)
        throws FileNotFoundException, DocumentException {

      User user = userRepository.findByAccountNumber(accountNumber);
      String customerName = user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName();

      Rectangle documentFormat = new Rectangle(PageSize.A4);
      Document document = new Document(documentFormat);
      log.info("setting the format of the document");
      OutputStream outputStream = new FileOutputStream(FILE);
      PdfWriter.getInstance(document, outputStream);
      document.open();
      
      PdfPTable bankInfoTable = new PdfPTable(1);
      PdfPCell bankNameCell = new PdfPCell(new Phrase("The Banking App"));
      bankNameCell.setBorder(0);
      bankNameCell.setBackgroundColor(BaseColor.BLUE);
      bankNameCell.setPadding(20f);
      PdfPCell bankAddressCell = new PdfPCell(new Phrase("77, Adeniyi Jones, Lagos Nigeria"));
      bankAddressCell.setBorder(0);
      bankInfoTable.addCell(bankNameCell);
      bankInfoTable.addCell(bankAddressCell);
      
      PdfPTable statementInfoTable = new PdfPTable(2);
      PdfPCell startDateCell = new PdfPCell(new Phrase("Start Date " + startDate));
      startDateCell.setBorder(0);
      PdfPCell statementCell = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT"));
      statementCell.setBorder(0);
      PdfPCell endDateCell = new PdfPCell(new Phrase("End Date: " + endDate));
      endDateCell.setBorder(0);
      PdfPCell customerNameCell = new PdfPCell(new Phrase(customerName));
      customerNameCell.setBorder(0);
      PdfPCell spaceCell = new PdfPCell();
      spaceCell.setBorder(0);
      PdfPCell customerAddressCell = new PdfPCell(new Phrase("Customer Address " + user.getAddress()));
      customerAddressCell.setBorder(0);
      
      PdfPTable transactionsTable = new PdfPTable(4);
      PdfPCell dateCell = new PdfPCell(new Phrase("DATE"));
      dateCell.setBackgroundColor(BaseColor.BLUE);
      dateCell.setBorder(0);
      PdfPCell transactionTypeCell = new PdfPCell(new Phrase("TRANSACTION TYPE"));
      transactionTypeCell.setBackgroundColor(BaseColor.BLUE);
      transactionTypeCell.setBorder(0);
      PdfPCell transactionAmountCell = new PdfPCell(new Phrase("Transaction Amount"));
      transactionAmountCell.setBackgroundColor(BaseColor.BLUE);
      transactionAmountCell.setBorder(0);
      PdfPCell statusCell = new PdfPCell(new Phrase("STATUS"));
      statusCell.setBackgroundColor(BaseColor.BLUE);
      statusCell.setBorder(0);
      
      transactionsTable.addCell(dateCell);
      transactionsTable.addCell(transactionTypeCell);
      transactionsTable.addCell(transactionAmountCell);
      transactionsTable.addCell(statusCell);
      
      transactions.forEach(transaction -> {
        transactionsTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
        transactionsTable.addCell(new Phrase(transaction.getTransactionType()));
        transactionsTable.addCell(new Phrase(transaction.getAmount().toString()));
        transactionsTable.addCell(new Phrase(transaction.getStatus()));
      });
      
      statementInfoTable.addCell(startDateCell);
      statementInfoTable.addCell(statementCell);
      statementInfoTable.addCell(endDateCell);
      statementInfoTable.addCell(customerNameCell);
      statementInfoTable.addCell(spaceCell);
      statementInfoTable.addCell(customerAddressCell);
      
      document.add(bankInfoTable);
      document.add(statementInfoTable);
      document.add(transactionsTable);
      
      document.close();
    }
  }
  
  private StatementDesigner getDesigner(){
    return new StatementDesigner();
  }
  
  public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate)
      throws DocumentException, FileNotFoundException {
    User user = userRepository.findByAccountNumber(accountNumber);    
    LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
    LocalDate endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
    List<Transaction> transactionList = transactionRepository.findAll().stream()
        .filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
        .filter(
            transaction -> transaction.getCreatedAt().isAfter(startLocalDate))
        .filter(
            transaction -> transaction.getCreatedAt().isBefore(endLocalDate))
        .toList();
        
    getDesigner().designStatement(transactionList, accountNumber, startDate, endDate);
    
          
    EmailDetails emailDetails = EmailDetails.builder()
        .recipient(user.getEmail())
        .subject("STATEMENT OF ACCOUNT")
        .messageBody("Kindly find your requested account statement attached")
        .attachment(FILE)
        .build();
          
      emailService.sendEmailWithAttachment(emailDetails);

    return transactionList;
  }
  
}

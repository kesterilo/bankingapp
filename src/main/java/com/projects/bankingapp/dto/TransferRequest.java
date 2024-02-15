package com.projects.bankingapp.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {
  private String sourceAccountNumber;
  private String destinationNumber;
  private BigDecimal amount;
}

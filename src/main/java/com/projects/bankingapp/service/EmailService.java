package com.projects.bankingapp.service;

import com.projects.bankingapp.dto.EmailDetails;

public interface EmailService {
  void sendEmailAlert(EmailDetails emailDetails);

  void sendEmailWithAttachment(EmailDetails emailDetails);
}

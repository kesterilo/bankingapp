package com.projects.bankingapp.dto;

import com.projects.bankingapp.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
  
  private String firstName;
  private String lastName;
  private String otherName;
  private String gender;
  private String address;
  private String stateOfOrigin;
  private String email;
  private String password;
  private String phoneNumber;
  private String alternativePhoneNumber;
  private Role role;
}

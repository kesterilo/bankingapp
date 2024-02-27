package com.projects.bankingapp.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.projects.bankingapp.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
  
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String usernameEmail) throws UsernameNotFoundException {
    return userRepository.findByEmail(usernameEmail).orElseThrow(() -> (new UsernameNotFoundException(usernameEmail + " not found")));
  }
  
}

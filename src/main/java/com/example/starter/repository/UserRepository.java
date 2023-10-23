package com.example.starter.repository;

import com.example.starter.model.User;

public class UserRepository {
  public User getUserByUsername(String username) {
    return new User(1, username);
  }
}

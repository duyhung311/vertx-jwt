package com.example.starter.exception;

public class TokenValidationException extends Exception {
  private String message;
  public TokenValidationException(String message) {
    super(message);
    this.message = message;
  }
}

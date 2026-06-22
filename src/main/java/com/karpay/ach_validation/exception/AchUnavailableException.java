package com.karpay.ach_validation.exception;

public class AchUnavailableException extends RuntimeException {
  public AchUnavailableException(String message) {
    super(message);
  }
}
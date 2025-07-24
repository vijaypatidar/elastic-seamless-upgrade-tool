package co.hyperflex.exceptions;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends AppException {

  public ForbiddenException(String message) {
    super(message, HttpStatus.FORBIDDEN);
  }

  public ForbiddenException() {
    super("Forbidden", HttpStatus.FORBIDDEN);
  }
}

package co.hyperflex.exceptions;

import org.springframework.http.HttpStatus;

public class UnprocessableEntityError extends AppException {

  public UnprocessableEntityError(String message) {
    super(message, HttpStatus.UNPROCESSABLE_ENTITY);
  }

  public UnprocessableEntityError() {
    super("Unprocessable Entity", HttpStatus.UNPROCESSABLE_ENTITY);
  }
}

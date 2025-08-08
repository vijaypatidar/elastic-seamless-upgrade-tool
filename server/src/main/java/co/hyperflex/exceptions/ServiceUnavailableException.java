package co.hyperflex.exceptions;

import org.springframework.http.HttpStatus;

public class ServiceUnavailableException extends AppException {

  public ServiceUnavailableException(String message) {
    super(message, HttpStatus.SERVICE_UNAVAILABLE);
  }

  public ServiceUnavailableException() {
    super("Service Unavailable", HttpStatus.SERVICE_UNAVAILABLE);
  }
}

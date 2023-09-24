package app.reminderappbackend.controller.advice;

import app.reminderappbackend.service.ReminderEntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import reminderapi.model.ResourceNotFoundError;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * ReminderEntityNotFoundException 発生時のエラーハンドラ
   *
   * @param ex
   * @return ResponseEntity<ResourceNotFoundError>
   */
  @ExceptionHandler(ReminderEntityNotFoundException.class)
  public ResponseEntity<ResourceNotFoundError> handle(ReminderEntityNotFoundException ex) {
    var error = new ResourceNotFoundError("Resource Not Found", ex.getMessage());

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }
}

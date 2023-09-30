package app.reminderappbackend.controller.advice;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import app.reminderappbackend.service.ReminderEntityNotFoundException;
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

  /**
   * MethodArgumentNotValidException 発生時のエラーハンドラ
   *
   * @param ex
   * @param headers
   * @param status
   * @param request
   * @return ResponseEntity<Object>
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
    MethodArgumentNotValidException ex,
    HttpHeaders headers,
    HttpStatusCode status,
    WebRequest request
  ) {
    var error = BadRequestErrorCreator.from(ex);

    return ResponseEntity.badRequest().body(error);
  }
}

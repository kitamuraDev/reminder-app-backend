package app.reminderappbackend.controller.advice;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ElementKind;
import reminderapi.model.BadRequestError;
import reminderapi.model.InvalidParam;

public class BadRequestErrorCreator {

  /**
   * エラー情報からエラーメッセージを取り出して、BadRequestErrorを組み立てる
   *
   * @param ex バリデーションエラーで投げられる例外
   * @return BadRequestError
   */
  public static BadRequestError from(MethodArgumentNotValidException ex) {
    var invalidParamList = createInvalidParamList(ex);

    var error = new BadRequestError();
    error.setInvalidParams(invalidParamList);

    return error;
  }

  private static List<InvalidParam> createInvalidParamList(MethodArgumentNotValidException ex) {
    return ex.getFieldErrors()
      .stream()
      .map(BadRequestErrorCreator::createInvalidParam) // Xxx::xxX は method reference と言う書き方
      .collect(Collectors.toList());
  }
  private static InvalidParam createInvalidParam(FieldError fieldError) {
    return new InvalidParam(fieldError.getField(), fieldError.getDefaultMessage());
  }

  /**
   * エラー情報からエラーメッセージを取り出して、BadRequestErrorを組み立てる
   *
   * @param ex バリデーションエラーで投げられる例外
   * @return BadRequestError
   */
  public static BadRequestError from(ConstraintViolationException ex) {
    var invalidParamList = createInvalidParamList(ex);

    var error = new BadRequestError();
    error.setInvalidParams(invalidParamList);

    return error;
  }

  private static List<InvalidParam> createInvalidParamList(ConstraintViolationException ex) {
    var invalidParamList = ex.getConstraintViolations().stream()
      .map(BadRequestErrorCreator::createInvalidParam)
      .collect(Collectors.toList());

    return invalidParamList;
  }

  private static InvalidParam createInvalidParam(ConstraintViolation<?> violation) {
    var parameterOpt = StreamSupport.stream(violation.getPropertyPath().spliterator(), false)
      .filter(node -> node.getKind().equals(ElementKind.PARAMETER))
      .findFirst();
    var invalidParam = new InvalidParam();
    parameterOpt.ifPresent(p -> invalidParam.setName(mapArgNameToParameter(p.getName())));
    invalidParam.setReason(violation.getMessage());
    return invalidParam;
  }

  private static String mapArgNameToParameter(String argName) {
    return (argName.equals("arg0")) ? "limit" : (argName.equals("arg1")) ? "offset" : "unknown";
  }

}

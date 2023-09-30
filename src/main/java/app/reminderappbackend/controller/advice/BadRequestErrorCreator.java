package app.reminderappbackend.controller.advice;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

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

}

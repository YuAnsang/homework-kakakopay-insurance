package com.github.asyu.homework.common.exception;

import com.github.asyu.homework.common.dto.response.ErrorResponse;
import com.github.asyu.homework.common.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(BindException.class)
  public ErrorResponse handleBindException(BindException e) {
    BindingResult bindingResult = e.getBindingResult();

    StringBuilder cause = new StringBuilder();
    for (FieldError fieldError : bindingResult.getFieldErrors()) {
      cause.append("field: [");
      cause.append(fieldError.getField());
      cause.append("]");
      cause.append(" input value: [");
      cause.append(fieldError.getRejectedValue());
      cause.append("]");
      cause.append(" message: [");
      cause.append(fieldError.getDefaultMessage());
      cause.append("]");
    }

    log.error("Validation Exception : {}", cause);
    return ErrorResponse.of(ErrorCode.INVALID_PARAMETER, cause.toString());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(InvalidRequestException.class)
  public ErrorResponse handleInvalidRequestException(InvalidRequestException e) {
    log.error(e.getMessage(), e);
    return ErrorResponse.of(ErrorCode.INVALID_PARAMETER_BUSINESS, e.getMessage());
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(EntityNotExistsException.class)
  public ErrorResponse handleEntityNotExistsException(EntityNotExistsException e) {
    log.error(e.getMessage(), e);
    return ErrorResponse.of(ErrorCode.NOT_FOUND);
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(BusinessException.class)
  public ErrorResponse handleRuntimeException(BusinessException e) {
    log.error(e.getMessage(), e);
    return ErrorResponse.of(ErrorCode.COMMON);
  }

}

package hjyun.sample.jwt.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler {

  private ResponseEntity<ApiError> createResponse(ApiError apiError) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

    return new ResponseEntity<>(apiError, headers, apiError.getStatus());
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<?> handleBusinessException(BusinessException e) {
    return createResponse(e.getApiError());
  }

  @ExceptionHandler({Exception.class, RuntimeException.class})
  public ResponseEntity<?> handleException(Exception e) {
    log.error(e.getMessage(), e);

    return createResponse(ApiError.of(
        ApiErrorCode.COMMON_INTERNAL_SERVER_ERROR));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<?> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException e) {
    Throwable cause = e.getCause();
    if (cause instanceof InvalidFormatException) {
      return createResponse(ApiError.of(
          ApiErrorCode.COMMON_INVALID_BODY, (InvalidFormatException) cause));
    }

    return handleException(e);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<?> handleHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException e) {
    return createResponse(ApiError.of(
        ApiErrorCode.COMMON_METHOD_NOT_ALLOWED));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    return createResponse(ApiError.of(
        ApiErrorCode.COMMON_INVALID_BODY, e.getBindingResult()));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<?> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException e) {
    return createResponse(ApiError.of(
        ApiErrorCode.COMMON_INVALID_PARAMETER, e));
  }

}

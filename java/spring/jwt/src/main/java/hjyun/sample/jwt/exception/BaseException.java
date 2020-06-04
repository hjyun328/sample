package hjyun.sample.jwt.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

  private final ApiError apiError;

  public BaseException(ApiError apiError) {
    super(apiError.getMessage());
    this.apiError = apiError;
  }

  public BaseException(ApiErrorCode apiErrorCode) {
    this(ApiError.of(apiErrorCode));
  }

}

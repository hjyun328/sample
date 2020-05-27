package hjyun.sample.jwt.exception;

public class BusinessException extends BaseException {

  public BusinessException(ApiErrorCode apiErrorCode) {
    super(apiErrorCode);
  }

}

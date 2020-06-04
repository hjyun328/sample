package hjyun.sample.jwt.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import hjyun.sample.jwt.exception.ApiError;
import hjyun.sample.jwt.exception.ApiErrorCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAccessDeniedHandler implements AccessDeniedHandler {

  private final ObjectMapper objectMapper;

  public JwtAccessDeniedHandler(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void handle(HttpServletRequest request,
                     HttpServletResponse response,
                     AccessDeniedException accessDeniedException) throws IOException {
    ApiError.response(response,
        ApiError.of(ApiErrorCode.COMMON_ACCESS_DENIED),
        objectMapper);
  }

}

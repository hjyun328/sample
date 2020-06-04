package hjyun.sample.jwt.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import hjyun.sample.jwt.exception.ApiError;
import hjyun.sample.jwt.exception.ApiErrorCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper objectMapper;

  public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void commence(HttpServletRequest request,
                       HttpServletResponse response,
                       AuthenticationException authException) throws IOException {
    ApiError.response(response,
        ApiError.of(ApiErrorCode.COMMON_ACCESS_DENIED),
        objectMapper);
  }

}

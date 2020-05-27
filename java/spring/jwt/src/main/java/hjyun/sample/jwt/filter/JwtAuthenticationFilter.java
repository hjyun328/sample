package hjyun.sample.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import hjyun.sample.jwt.component.JwtTokenProvider;
import hjyun.sample.jwt.exception.ApiError;
import hjyun.sample.jwt.domain.user.dto.UserDto;
import hjyun.sample.jwt.exception.ApiErrorCode;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;
  private final ObjectMapper objectMapper;

  public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                 JwtTokenProvider jwtTokenProvider,
                                 ObjectMapper objectMapper) {
    this.authenticationManager = authenticationManager;
    this.jwtTokenProvider = jwtTokenProvider;
    this.objectMapper = objectMapper;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
                                              HttpServletResponse response)
      throws AuthenticationException {
    final UserDto userDto;

    try {
      userDto = new ObjectMapper().readValue(request.getInputStream(), UserDto.class);
    } catch (IOException e) {
      throw new InternalAuthenticationServiceException(e.getMessage(), e);
    }

    return authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            userDto.getUsername(), userDto.getPassword(), Collections.emptyList()));
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request,
                                          HttpServletResponse response,
                                          FilterChain chain,
                                          Authentication authResult) {
    jwtTokenProvider.applyHeader(response, (UserDetails) authResult.getPrincipal());
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            AuthenticationException failed) throws IOException {
    ApiError.response(response,
        ApiError.of(ApiErrorCode.COMMON_UNAUTHORIZED),
        objectMapper);
  }

}

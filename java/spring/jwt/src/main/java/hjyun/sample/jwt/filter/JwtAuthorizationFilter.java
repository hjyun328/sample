package hjyun.sample.jwt.filter;

import hjyun.sample.jwt.bean.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

  private final JwtTokenProvider jwtTokenProvider;

  public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
                                JwtTokenProvider jwtTokenProvider) {
    super(authenticationManager);

    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain chain) throws IOException, ServletException {
    String token = jwtTokenProvider.resolve(request);

    if (jwtTokenProvider.validate(token)) {
      Claims claims = jwtTokenProvider.getClaims(token);
      UserDetails userDetails = jwtTokenProvider.getUserDetails(claims);

      if (userDetails != null) {
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                null,
                userDetails.getAuthorities()));

        if (jwtTokenProvider.canRefresh(claims)) {
          jwtTokenProvider.applyHeader(response, userDetails);
        }
      }
    }

    chain.doFilter(request, response);
  }

}

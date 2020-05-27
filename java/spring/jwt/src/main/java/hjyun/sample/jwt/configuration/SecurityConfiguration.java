package hjyun.sample.jwt.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import hjyun.sample.jwt.component.JwtAccessDeniedHandler;
import hjyun.sample.jwt.component.JwtAuthenticationEntryPoint;
import hjyun.sample.jwt.component.JwtTokenProvider;
import hjyun.sample.jwt.filter.JwtAuthenticationFilter;
import hjyun.sample.jwt.filter.JwtAuthorizationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final ObjectMapper objectMapper;
  private final UserDetailsService userDetailsService;

  public SecurityConfiguration(PasswordEncoder passwordEncoder,
                               JwtTokenProvider jwtTokenProvider,
                               JwtAccessDeniedHandler jwtAccessDeniedHandler,
                               JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                               ObjectMapper objectMapper,
                               UserDetailsService userDetailsService) {
    this.passwordEncoder = passwordEncoder;
    this.jwtTokenProvider = jwtTokenProvider;
    this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    this.objectMapper = objectMapper;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf()
        .disable();

    http.httpBasic()
        .disable();

    http.sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http.headers()
        .frameOptions()
        .sameOrigin();

    http.authorizeRequests()
        .antMatchers(HttpMethod.POST,
            "/api/*/users", "/login").permitAll()
        .antMatchers("/api/**").authenticated()
        .anyRequest().permitAll();

    http.exceptionHandling()
        .accessDeniedHandler(jwtAccessDeniedHandler)
        .authenticationEntryPoint(jwtAuthenticationEntryPoint);

    JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
        authenticationManager(), jwtTokenProvider, objectMapper);
    jwtAuthenticationFilter.setFilterProcessesUrl("/login");

    JwtAuthorizationFilter jwtAuthorizationFilter = new JwtAuthorizationFilter(
        authenticationManager(), jwtTokenProvider);

    http.addFilter(jwtAuthenticationFilter)
        .addFilter(jwtAuthorizationFilter);
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder);
  }

}

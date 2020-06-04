package hjyun.sample.jwt.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import hjyun.sample.jwt.bean.JwtAccessDeniedHandler;
import hjyun.sample.jwt.bean.JwtAuthenticationEntryPoint;
import hjyun.sample.jwt.bean.JwtTokenProvider;
import hjyun.sample.jwt.bean.UserDetailsService;
import hjyun.sample.jwt.bean.UserPasswordEncoder;
import hjyun.sample.jwt.domain.user.repository.UserRepository;
import hjyun.sample.jwt.filter.JwtAuthenticationFilter;
import hjyun.sample.jwt.filter.JwtAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

  private final ObjectMapper objectMapper;

  private final UserRepository userRepository;

  public SecurityConfiguration(ObjectMapper objectMapper,
                               UserRepository userRepository) {
    this.objectMapper = objectMapper;
    this.userRepository = userRepository;
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
        .accessDeniedHandler(jwtAccessDeniedHandler())
        .authenticationEntryPoint(jwtAuthenticationEntryPoint());

    JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
        authenticationManager(), jwtTokenProvider(), objectMapper);
    jwtAuthenticationFilter.setFilterProcessesUrl("/login");

    JwtAuthorizationFilter jwtAuthorizationFilter = new JwtAuthorizationFilter(
        authenticationManager(), jwtTokenProvider());

    http.addFilter(jwtAuthenticationFilter)
        .addFilter(jwtAuthorizationFilter);
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService())
        .passwordEncoder(passwordEncoder());
  }

  @Bean
  public UserDetailsService userDetailsService() {
    return new UserDetailsService(userRepository);
  }

  @Bean
  public JwtAccessDeniedHandler jwtAccessDeniedHandler() {
    return new JwtAccessDeniedHandler(objectMapper);
  }

  @Bean
  public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
    return new JwtAuthenticationEntryPoint(objectMapper);
  }

  @Bean
  public JwtTokenProvider jwtTokenProvider() {
    return new JwtTokenProvider();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new UserPasswordEncoder();
  }

}

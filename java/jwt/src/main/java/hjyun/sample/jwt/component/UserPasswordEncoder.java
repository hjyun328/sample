package hjyun.sample.jwt.component;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserPasswordEncoder implements PasswordEncoder {

  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @Override
  public String encode(CharSequence rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }

  @Override
  public boolean upgradeEncoding(String encodedPassword) {
    return passwordEncoder.upgradeEncoding(encodedPassword);
  }

}

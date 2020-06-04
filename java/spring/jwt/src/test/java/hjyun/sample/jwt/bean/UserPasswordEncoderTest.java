package hjyun.sample.jwt.bean;

import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class UserPasswordEncoderTest {

  private final PasswordEncoder passwordEncoder = new UserPasswordEncoder();

  @Test
  public void encode() {
    // given
    String rawPassword = "foo";

    // when
    String firstEncodedPassword = passwordEncoder.encode(rawPassword);
    String secondEncodedPassword = passwordEncoder.encode(firstEncodedPassword);

    // then
    assertThat(rawPassword, is(not(firstEncodedPassword)));
    assertThat(rawPassword, is(not(secondEncodedPassword)));
    assertThat(firstEncodedPassword, is(not(secondEncodedPassword)));
  }

  @Test
  public void matches() {
    // given
    String rawPassword = "foo";
    String invalidRawPassword = "bar";

    // when
    String encodedPassword = passwordEncoder.encode(rawPassword);

    // then
    assertThat(passwordEncoder.matches(rawPassword, encodedPassword), is(true));
    assertThat(passwordEncoder.matches(invalidRawPassword, encodedPassword), is(false));
  }

}

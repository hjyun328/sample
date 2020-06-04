package hjyun.sample.jwt.bean;

import hjyun.sample.jwt.domain.user.entity.RoleEntity;
import hjyun.sample.jwt.domain.user.entity.UserEntity;
import hjyun.sample.jwt.domain.user.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UserDetailsServiceTest {

  @InjectMocks
  private UserDetailsService userDetailsService;

  @Mock
  private UserRepository userRepository;

  @Test
  public void loadUserByUsername() {
    // given
    UserEntity userEntity =
        UserEntity.builder()
          .username("foo")
          .password("bar")
          .roles(List.of(RoleEntity.ROLE_ADMIN, RoleEntity.ROLE_USER))
          .build();

    User user = new User(
        userEntity.getUsername(),
        userEntity.getPassword(),
        userEntity.getRoles());

    given(userRepository.findByUsername(user.getUsername()))
        .willReturn(Optional.of(userEntity));

    // when
    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

    // then
    verify(userRepository, atMostOnce()).findByUsername(user.getUsername());
    assertThat(userDetails.getUsername(), is(user.getUsername()));
    assertThat(userDetails.getPassword(), is(user.getPassword()));
    assertThat(userDetails.getAuthorities(), is(user.getAuthorities()));
  }

  @Test(expected = UsernameNotFoundException.class)
  public void getUserDetails_notFound() {
    // given
    String username = "foo";

    given(userRepository.findByUsername(username)).willReturn(Optional.empty());

    // when
    userDetailsService.loadUserByUsername(username);
  }

}

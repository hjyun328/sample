package hjyun.sample.jwt.domain.user.service;

import hjyun.sample.jwt.domain.user.dto.UserDto;
import hjyun.sample.jwt.domain.user.entity.RoleEntity;
import hjyun.sample.jwt.domain.user.entity.UserEntity;
import hjyun.sample.jwt.exception.BusinessException;
import hjyun.sample.jwt.domain.user.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

// TODO: more test
@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  private static final String ENCODED_PASSWORD = "foo";

  @Test
  public void create_passwordEncoded() throws BusinessException {
    // given
    final UserDto userDto = UserDto.builder()
        .password("bar")
        .build();

    given(passwordEncoder.encode(any())).willReturn(ENCODED_PASSWORD);

    // when
    final UserDto createdUserDto = userService.create(userDto);

    // then
    assertThat(createdUserDto.getPassword(), is(ENCODED_PASSWORD));
  }

  @Test(expected = BusinessException.class)
  public void create_usernameExists() throws BusinessException {
    // given
    given(userRepository.exists(any())).willReturn(true);

    // when
    userService.create(UserDto.builder().build());
  }

  @Test(expected = BusinessException.class)
  public void create_emailExists() throws BusinessException {
    given(userRepository.exists(any())).willReturn(true);

    // when
    userService.create(UserDto.builder().build());
  }

  @Test(expected = BusinessException.class)
  public void get_notFound() throws BusinessException {
    // given
    given(userRepository.findById(any())).willReturn(Optional.empty());

    // when
    userService.get(1L);
  }

  @Test(expected = BusinessException.class)
  public void getByUsername_notFound() throws BusinessException {
    // given
    given(userRepository.findByUsername(any())).willReturn(Optional.empty());

    // when
    userService.getByUsername("foo");
  }

  @Test(expected = BusinessException.class)
  public void delete_notFound() throws BusinessException {
    // given
    given(userRepository.existsById(any())).willReturn(false);

    // when
    userService.delete(1L);
  }

  @Test
  public void getUserDetails() throws BusinessException {
    // given
    final UserEntity userEntity =
        UserEntity.builder()
          .username("foo")
          .password("bar")
          .roles(List.of(RoleEntity.ROLE_ADMIN, RoleEntity.ROLE_USER))
          .build();

    final User user = new User(
        userEntity.getUsername(),
        userEntity.getPassword(),
        userEntity.getRoles());

    given(userRepository.findByUsername(any())).willReturn(Optional.of(userEntity));

    // when
    final UserDetails userDetails = userService.getUserDetails(userEntity.getUsername());

    // then
    assertThat(userDetails.getUsername(), is(user.getUsername()));
    assertThat(userDetails.getPassword(), is(user.getPassword()));
    assertThat(userDetails.getAuthorities(), is(user.getAuthorities()));
  }

  @Test(expected = BusinessException.class)
  public void getUserDetails_notFound() throws BusinessException {
    // given
    given(userRepository.findByUsername(any())).willReturn(Optional.empty());

    // when
    userService.getUserDetails("foo");

    // then
  }

}

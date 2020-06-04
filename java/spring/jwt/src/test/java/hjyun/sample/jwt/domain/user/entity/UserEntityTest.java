package hjyun.sample.jwt.domain.user.entity;

import hjyun.sample.jwt.domain.user.dto.UserDto;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class UserEntityTest {

  @Test
  public void builder() {
    // given
    String username = "foo";
    String email = "foo@bar.com";
    String password = "baz";
    Collection<RoleEntity> roles = List.of(RoleEntity.ROLE_ADMIN, RoleEntity.ROLE_USER);

    // when
    UserEntity userEntity = UserEntity.builder()
        .username(username)
        .email(email)
        .password(password)
        .roles(roles)
        .build();

    // then
    assertThat(userEntity.getUsername(), is(username));
    assertThat(userEntity.getEmail(), is(email));
    assertThat(userEntity.getPassword(), is(password));
    assertThat(userEntity.getRoles(), is(roles));
  }

  @Test
  public void of() {
    // given
    UserDto userDto = UserDto.builder()
        .id(1L)
        .username("foo")
        .email("foo@bar.com")
        .password("baz")
        .roles(List.of(RoleEntity.ROLE_ADMIN.name(), RoleEntity.ROLE_USER.name()))
        .build();

    // when
    UserEntity userEntity = UserEntity.of(userDto);

    // then
    assertThat(userEntity.getId(), is(not(userDto.getId())));
    assertThat(userEntity.getUsername(), is(userDto.getUsername()));
    assertThat(userEntity.getEmail(), is(userDto.getEmail()));
    assertThat(userEntity.getPassword(), is(userDto.getPassword()));
    assertThat(userEntity.getRoles(), is(userDto.getRoles().stream()
        .map(RoleEntity::valueOf).collect(Collectors.toList())));
  }

  @Test
  public void updateEmail() {
    // given
    UserEntity userEntity = UserEntity.builder().email("foo").build();

    // when
    userEntity.updateEmail("bar");

    // then
    assertThat(userEntity.getEmail(), is("bar"));
  }

  @Test
  public void updatePassword() {
    // given
    UserEntity userEntity = UserEntity.builder().password("foo").build();

    // when
    userEntity.updatePassword("bar");

    // then
    assertThat(userEntity.getPassword(), is("bar"));
  }

}

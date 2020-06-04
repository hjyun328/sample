package hjyun.sample.jwt.domain.user.dto;

import hjyun.sample.jwt.domain.user.entity.UserEntity;
import hjyun.sample.jwt.util.ModelMapperUtil;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserDto {

  @Builder
  public UserDto(Long id,
                 String username,
                 String email,
                 String password,
                 String newPassword,
                 Collection<String> roles) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.password = password;
    this.newPassword = newPassword;
    this.roles = roles;
  }

  private Long id;

  @Size(min = SIZE_MIN_USERNAME, max = SIZE_MAX_USERNAME)
  @NotEmpty
  private String username;

  @Email
  @NotEmpty
  private String email;

  @Size(min = SIZE_MIN_PASSWORD, max = SIZE_MAX_PASSWORD)
  @NotEmpty
  private String password;

  @Size(min = SIZE_MIN_PASSWORD, max = SIZE_MAX_PASSWORD)
  @NotEmpty
  private String newPassword;

  private Collection<String> roles;

  public static UserDto of(UserEntity userEntity) {
    UserDto userDto = ModelMapperUtil.map(
        userEntity, UserDto.class);

    userDto.password = null;

    userDto.roles = userEntity.getRoles()
        .stream().map(Enum::name)
        .collect(Collectors.toList());

    return userDto;
  }

  public static List<UserDto> of(List<UserEntity> userEntities) {
    return userEntities.stream().collect(
        ArrayList::new,
        (userDtos, userEntity) -> userDtos.add(of(userEntity)),
        List::addAll);
  }

  public static final int SIZE_MIN_USERNAME = 4;
  public static final int SIZE_MAX_USERNAME = 32;

  public static final int SIZE_MIN_PASSWORD = 8;
  public static final int SIZE_MAX_PASSWORD = 64;

}

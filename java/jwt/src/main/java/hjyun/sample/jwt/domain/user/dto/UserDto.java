package hjyun.sample.jwt.domain.user.dto;

import hjyun.sample.jwt.domain.user.entity.RoleEntity;
import hjyun.sample.jwt.domain.user.entity.UserEntity;
import hjyun.sample.jwt.util.ModelMapperUtil;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
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
    this.password = newPassword;
    this.roles = roles;
  }

  private Long id;

  @Size(min = 4, max = 32)
  @NotEmpty
  private String username;

  @Email
  @NotEmpty
  private String email;

  @Size(min = 8, max = 64)
  @NotEmpty
  private String password;

  @Size(min = 8, max = 64)
  private String newPassword;

  private Collection<String> roles;

  public void encodePassword(String encodedPassword) {
    password = encodedPassword;
  }

  public void applyAdminRole() {
    roles = List.of(RoleEntity.ROLE_ADMIN.name(), RoleEntity.ROLE_USER.name());
  }

  public static UserDto of(UserEntity userEntity) {
    final UserDto userDto = ModelMapperUtil.map(
        userEntity, UserDto.class);

    userDto.password = null;

    userDto.roles = userEntity.getRoles()
        .stream().map(RoleEntity::name)
        .collect(Collectors.toList());

    return userDto;
  }

}

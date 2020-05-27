package hjyun.sample.jwt.domain.user.entity;

import hjyun.sample.jwt.domain.user.dto.UserDto;
import hjyun.sample.jwt.util.ModelMapperUtil;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import java.util.Collection;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserEntity {

  @Builder
  public UserEntity(String username,
                    String email,
                    String password,
                    Collection<RoleEntity> roles) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.roles = roles;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @ElementCollection
  @JoinTable (
      name = "roles",
      joinColumns = {
        @JoinColumn(name = "id")
      }
  )
  @Enumerated(EnumType.STRING)
  @Column(name = "role")
  private Collection<RoleEntity> roles;

  public void updateUsername(String username) {
    this.username = username;
  }

  public void updateEmail(String email) {
    this.email = email;
  }

  public void updatePassword(String password) {
    this.password = password;
  }

  public static UserEntity of(UserDto userDto) {
    final UserEntity userEntity = ModelMapperUtil.map(
        userDto, UserEntityBuilder.class).build();

    userEntity.roles = userDto.getRoles()
        .stream()
        .map(RoleEntity::valueOf) // FIXME: may IllegalArgumentException
        .collect(Collectors.toList());

    return userEntity;
  }

}

package hjyun.sample.jwt.domain.user.entity;

import org.springframework.security.core.GrantedAuthority;

public enum RoleEntity implements GrantedAuthority {

  ROLE_ADMIN,
  ROLE_USER;

  @Override
  public String getAuthority() {
    return name();
  }

}

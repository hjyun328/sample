package hjyun.sample.jwt.domain.user.service;

import hjyun.sample.jwt.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService
    implements org.springframework.security.core.userdetails.UserDetailsService {

  private final UserService userService;

  public UserDetailsService(UserService userService) {
    this.userService = userService;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    try {
      return userService.getUserDetails(username);
    } catch (BusinessException e) {
      throw new UsernameNotFoundException(StringUtils.EMPTY);
    }
  }

}

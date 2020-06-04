package hjyun.sample.jwt.bean;

import hjyun.sample.jwt.domain.user.entity.UserEntity;
import hjyun.sample.jwt.domain.user.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

public class UserDetailsService
    implements org.springframework.security.core.userdetails.UserDetailsService {

  private final UserRepository userRepository;

  public UserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserEntity userEntity = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException(StringUtils.EMPTY));

    return new User(
        userEntity.getUsername(),
        userEntity.getPassword(),
        userEntity.getRoles());
  }

}

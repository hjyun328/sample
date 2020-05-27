package hjyun.sample.jwt.domain.user.service;

import hjyun.sample.jwt.domain.user.dto.UserDto;
import hjyun.sample.jwt.domain.user.entity.UserEntity;
import hjyun.sample.jwt.exception.ApiErrorCode;
import hjyun.sample.jwt.exception.BusinessException;
import hjyun.sample.jwt.domain.user.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public UserDto create(UserDto userDto) throws BusinessException {
    checkExistsByUsername(userDto.getUsername());
    checkExistsByEmail(userDto.getEmail());

    userDto.encodePassword(passwordEncoder.encode(userDto.getPassword()));
    userDto.applyAdminRole();

    final UserEntity userEntity = UserEntity.of(userDto);
    userRepository.save(userEntity);

    return UserDto.of(userEntity);
  }

  @Transactional
  public UserDto update(long id, UserDto userDto)
      throws BusinessException {
    // FIXME: update by dto? or entity?
    final UserEntity userEntity = getEntity(id);

    if (!passwordEncoder.matches(userDto.getPassword(), userEntity.getPassword())) {
      throw new BusinessException(ApiErrorCode.USER_PASSWORD_MISMATCH);
    }

    if (!StringUtils.equals(userDto.getUsername(), userEntity.getUsername())) {
      checkExistsByUsername(userDto.getUsername());
      userEntity.updateUsername(userDto.getUsername());
    }

    if (!StringUtils.equals(userDto.getEmail(), userEntity.getEmail())) {
      checkExistsByEmail(userDto.getEmail());
      userEntity.updateEmail(userDto.getEmail());
    }

    if (!passwordEncoder.matches(userDto.getNewPassword(), userEntity.getPassword())) {
      userDto.encodePassword(passwordEncoder.encode(userDto.getNewPassword()));
      userEntity.updatePassword(userDto.getPassword());
    }

    // TODO: update roles
    return UserDto.of(userEntity);
  }

  public UserDto get(long id) throws BusinessException {
    return UserDto.of(getEntity(id));
  }

  public UserDto getByUsername(String username) throws BusinessException {
    return UserDto.of(getEntityByUsername(username));
  }

  @Transactional
  public void delete(long id) throws BusinessException {
    if (!userRepository.existsById(id)) {
      throw new BusinessException(ApiErrorCode.USER_NOT_FOUND);
    }

    userRepository.deleteById(id);
  }

  public org.springframework.security.core.userdetails.User getUserDetails(String username)
      throws BusinessException {
    final UserEntity userEntity = getEntityByUsername(username);

    return new org.springframework.security.core.userdetails.User(
        userEntity.getUsername(),
        userEntity.getPassword(),
        userEntity.getRoles());
  }

  private UserEntity getEntity(long id) throws BusinessException {
    return userRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ApiErrorCode.USER_NOT_FOUND));
  }

  private UserEntity getEntityByUsername(String username) throws BusinessException {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new BusinessException(ApiErrorCode.USER_USERNAME_NOT_FOUND));
  }

  private void checkExistsByUsername(String username) throws BusinessException {
    if (userRepository.exists(Example.of(
        UserEntity.builder().username(username).build(),
        ExampleMatcher.matching().withIgnoreCase()))) {
      throw new BusinessException(ApiErrorCode.USER_USERNAME_DUPLICATED);
    }
  }

  private void checkExistsByEmail(String email) throws BusinessException {
    if (userRepository.exists(Example.of(
        UserEntity.builder().email(email).build(),
        ExampleMatcher.matching().withIgnoreCase()))) {
      throw new BusinessException(ApiErrorCode.USER_EMAIL_DUPLICATED);
    }
  }

}

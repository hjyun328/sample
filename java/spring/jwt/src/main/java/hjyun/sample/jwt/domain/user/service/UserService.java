package hjyun.sample.jwt.domain.user.service;

import hjyun.sample.jwt.domain.user.dto.UserDto;
import hjyun.sample.jwt.domain.user.entity.UserEntity;
import hjyun.sample.jwt.domain.user.repository.UserRepository;
import hjyun.sample.jwt.exception.ApiErrorCode;
import hjyun.sample.jwt.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
  public UserDto create(UserDto userDto) {
    checkDuplicateUsername(userDto.getUsername());
    checkDuplicateEmail(userDto.getEmail());

    UserEntity userEntity = UserEntity.of(userDto);
    userEntity.updatePassword(passwordEncoder.encode(userDto.getPassword()));
    userEntity.applyAdminRole();  // TODO: 어드민은 최대 하나만 존재해야 함

    userRepository.save(userEntity);

    return UserDto.of(userEntity);
  }

  @Transactional
  public UserDto update(long id, UserDto userDto) {
    // FIXME: userRepository.save 호출하여 수정? 아니면 영속성 컨텍스트에서 entity field 수정?
    UserEntity userEntity = getEntity(id);

    if (!passwordEncoder.matches(userDto.getPassword(), userEntity.getPassword())) {
      throw new BusinessException(ApiErrorCode.USER_PASSWORD_MISMATCH);
    }

    if (!StringUtils.equals(userDto.getEmail(), userEntity.getEmail())) {
      checkDuplicateEmail(userDto.getEmail());
      userEntity.updateEmail(userDto.getEmail());
    }

    if (!passwordEncoder.matches(userDto.getNewPassword(), userEntity.getPassword())) {
      userEntity.updatePassword(passwordEncoder.encode(userDto.getNewPassword()));
    }

    // TODO: roles 업데이트 필요
    return UserDto.of(userEntity);
  }

  public UserDto get(long id) {
    return UserDto.of(getEntity(id));
  }

  public List<UserDto> getAll() {
    return UserDto.of(getAllEntity());
  }

  public UserDto getByUsername(String username) {
    return UserDto.of(getEntityByUsername(username));
  }

  @Transactional
  public void delete(long id) {
    if (!userRepository.existsById(id)) {
      throw new BusinessException(ApiErrorCode.USER_NOT_FOUND);
    }

    userRepository.deleteById(id);
  }

  private void checkDuplicateUsername(String username) {
    if (userRepository.exists(Example.of(
        UserEntity.builder().username(username).build(),
        ExampleMatcher.matching().withIgnoreCase()))) {
      throw new BusinessException(ApiErrorCode.USER_USERNAME_DUPLICATED);
    }
  }

  private void checkDuplicateEmail(String email) {
    if (userRepository.exists(Example.of(
        UserEntity.builder().username(email).build(),
        ExampleMatcher.matching().withIgnoreCase()))) {
      throw new BusinessException(ApiErrorCode.USER_EMAIL_DUPLICATED);
    }
  }

  private UserEntity getEntity(long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new BusinessException(ApiErrorCode.USER_NOT_FOUND));
  }

  private UserEntity getEntityByUsername(String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new BusinessException(ApiErrorCode.USER_USERNAME_NOT_FOUND));
  }

  private List<UserEntity> getAllEntity() {
    List<UserEntity> users = new ArrayList<>(userRepository.findAll());
    if (users.isEmpty()) {
      throw new BusinessException(ApiErrorCode.NO_USER);
    }
    return users;
  }

}

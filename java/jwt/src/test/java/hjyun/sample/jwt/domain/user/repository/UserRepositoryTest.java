package hjyun.sample.jwt.domain.user.repository;

import hjyun.sample.jwt.domain.user.entity.RoleEntity;
import hjyun.sample.jwt.domain.user.entity.UserEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

// TODO: more test
@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  private UserEntity.UserEntityBuilder userEntityBuilder;

  @Before
  public void before() {
    userEntityBuilder =
        UserEntity.builder()
          .username("foo")
          .email("foo@bar.com")
          .password("foobar")
          .roles(List.of(RoleEntity.ROLE_ADMIN, RoleEntity.ROLE_USER));
  }

  @Test(expected = DataIntegrityViolationException.class)
  public void save_usernameEmpty() {
    userEntityBuilder.username(null);

    userRepository.save(userEntityBuilder.build());
  }

  @Test(expected = DataIntegrityViolationException.class)
  public void save_passwordEmpty() {
    userEntityBuilder.password(null);

    userRepository.save(userEntityBuilder.build());
  }

  @Test(expected = DataIntegrityViolationException.class)
  public void save_emailEmpty() {
    userEntityBuilder.email(null);

    userRepository.save(userEntityBuilder.build());
  }

}

package hjyun.sample.jwt.domain.user.repository;

import hjyun.sample.jwt.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  /*
  TODO: remove jpql. replaced by ExampleMatcher.
  @Query(
      "SELECT CASE WHEN COUNT(u) > 0 "
      + "THEN TRUE "
      + "ELSE FALSE "
      + "END "
      + "FROM UserEntity u "
      + "WHERE LOWER(u.email) = LOWER(:email)")
  boolean existsByEmail(@Param("email") String email);
   */

  /*
  @Query(
      "SELECT CASE WHEN COUNT(u) > 0 "
      + "THEN TRUE "
      + "ELSE FALSE "
      + "END "
      + "FROM UserEntity u "
      + "WHERE LOWER(u.username) = LOWER(:username)")
  boolean existsByUsername(@Param("username") String username);
   */

  Optional<UserEntity> findByUsername(String username);

}

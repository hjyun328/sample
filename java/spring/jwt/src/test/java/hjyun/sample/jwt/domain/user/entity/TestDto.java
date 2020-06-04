package hjyun.sample.jwt.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestDto {

  private long id;

  @Size(min = SIZE_MIN_NAME, max = SIZE_MAX_NAME)
  private String name;

  public static final int SIZE_MIN_NAME = 4;
  public static final int SIZE_MAX_NAME = 6;

}

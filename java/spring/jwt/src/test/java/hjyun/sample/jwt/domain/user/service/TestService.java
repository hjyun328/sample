package hjyun.sample.jwt.domain.user.service;

import hjyun.sample.jwt.domain.user.entity.TestDto;
import org.springframework.stereotype.Service;

@Service
public class TestService {

  public TestDto test() throws Exception {
    return new TestDto();
  }

}

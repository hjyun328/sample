package hjyun.sample.jwt.bean;

import hjyun.sample.jwt.domain.common.controller.BaseControllerTest;
import hjyun.sample.jwt.domain.common.controller.TestController;
import hjyun.sample.jwt.domain.user.dto.UserDto;
import hjyun.sample.jwt.domain.user.service.TestService;
import hjyun.sample.jwt.exception.ApiErrorCode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TestController.class)
public class JwtAccessDeniedHandlerTest extends BaseControllerTest {

  private static final String URL = "/api/v1/test";

  @Autowired
  private PasswordEncoder passwordEncoder;

  @MockBean
  private TestService testService;

  @Test
  public void commence() throws Exception {
    // given
    UserDto userDto = UserDto.builder()
        .username("foofoo")
        .password("barbar")
        .build();

    // when
    ResultActions resultActions = post(URL, userDto);

    // then
    resultActions
        .andExpect(status().isForbidden())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").value(
            ApiErrorCode.COMMON_ACCESS_DENIED.code()))
        .andExpect(jsonPath("$.message").value(
            ApiErrorCode.COMMON_ACCESS_DENIED.message()))
        .andExpect(jsonPath("$.details", is(empty())))
        .andDo(print());
  }

}

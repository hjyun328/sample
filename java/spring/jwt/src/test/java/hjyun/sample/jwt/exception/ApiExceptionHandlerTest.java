package hjyun.sample.jwt.exception;

import hjyun.sample.jwt.domain.common.controller.BaseControllerTest;
import hjyun.sample.jwt.domain.common.controller.TestController;
import hjyun.sample.jwt.domain.user.entity.TestDto;
import hjyun.sample.jwt.domain.user.service.TestService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasEntry;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(TestController.class)
@WithMockUser(username = "test")
public class ApiExceptionHandlerTest extends BaseControllerTest {

  private static final String URL = "/api/v1/test";

  @MockBean
  private TestService testService;

  @Test
  public void handleBusinessException() throws Exception {
    // given
    given(testService.test()).willThrow(new BusinessException(
        ApiErrorCode.COMMON_INTERNAL_SERVER_ERROR));

    // when
    ResultActions resultActions = get(URL);

    // then
    resultActions
        .andExpect(status().isInternalServerError())
        .andDo(print());
  }

  @Test
  public void handleException() throws Exception {
    // given
    given(testService.test()).willThrow(new Exception());

    // when
    ResultActions resultActions = get(URL);

    // then
    resultActions
        .andExpect(status().isInternalServerError())
        .andDo(print());
  }

  @Test
  public void handleRuntimeException() throws Exception {
    // given
    given(testService.test()).willThrow(new RuntimeException());

    // when
    ResultActions resultActions = get(URL);

    // then
    resultActions
        .andExpect(status().isInternalServerError())
        .andDo(print());
  }

  @Test
  public void handleHttpMessageNotReadableException() throws Exception {
    // given
    String json = "{ \"id\": \"foo\" }";

    // when
    ResultActions resultActions = post(URL, json);

    // then
    resultActions
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").value(ApiErrorCode.COMMON_INVALID_BODY.code()))
        .andExpect(jsonPath("$.message").value(ApiErrorCode.COMMON_INVALID_BODY.message()))
        .andExpect(jsonPath("$.details", contains(
            allOf(
                hasEntry("name", "id"),
                hasEntry("value", "foo"),
                hasEntry("reason", String.format(
                    ApiErrorCode.COMMON_INVALID_TYPE.message(), "long"))))
        )).andDo(print());
  }

  @Test
  public void handleHttpRequestMethodNotSupportedException() throws Exception {
    // when
    ResultActions resultActions = delete(URL);

    // then
    resultActions
        .andExpect(status().isMethodNotAllowed())
        .andDo(print());
  }

  @Test
  public void handleMethodArgumentNotValidException() throws Exception {
    // given
    TestDto testDto = new TestDto(1L, StringUtils.repeat('n', TestDto.SIZE_MIN_NAME - 1));

    // when
    ResultActions resultActions = post(URL, testDto);

    // then
    resultActions
    .andExpect(status().isBadRequest())
    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    .andExpect(jsonPath("$.code").value(ApiErrorCode.COMMON_INVALID_BODY.code()))
    .andExpect(jsonPath("$.message").value(ApiErrorCode.COMMON_INVALID_BODY.message()))
    .andExpect(jsonPath("$.details", containsInAnyOrder(
        allOf(
            hasEntry("name", "name"),
            hasEntry("value", testDto.getName()),
            hasEntry("reason", String.format("size must be between %d and %d",
                TestDto.SIZE_MIN_NAME, TestDto.SIZE_MAX_NAME)))
        ))).andDo(print());
  }

  @Test
  public void handleMethodArgumentTypeMismatchException() {
    // TODO: test
  }

}

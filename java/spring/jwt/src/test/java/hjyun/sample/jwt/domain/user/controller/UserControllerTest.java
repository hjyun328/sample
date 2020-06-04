package hjyun.sample.jwt.domain.user.controller;

import hjyun.sample.jwt.domain.common.controller.BaseControllerTest;
import hjyun.sample.jwt.domain.user.dto.UserDto;
import hjyun.sample.jwt.domain.user.service.UserService;
import hjyun.sample.jwt.exception.ApiErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasEntry;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
@WithMockUser(username = "test")
public class UserControllerTest extends BaseControllerTest {

  private static final String URL = "/api/v1/users";

  @MockBean
  public UserService userService;

  private UserDto.UserDtoBuilder userDtoBuilder;

  @Before
  public void before() {
    userDtoBuilder = UserDto.builder()
        .id(1L)
        .username(StringUtils.repeat('u', UserDto.SIZE_MIN_USERNAME))
        .password(StringUtils.repeat('p', UserDto.SIZE_MIN_PASSWORD))
        .newPassword(StringUtils.repeat('n', UserDto.SIZE_MIN_PASSWORD))
        .email("foo@bar.com");
  }

  @Test
  public void invalidSizeMin() throws Exception {
    // given
    UserDto userDto = userDtoBuilder
        .username(StringUtils.repeat('u', UserDto.SIZE_MIN_USERNAME - 1))
        .password(StringUtils.repeat('p', UserDto.SIZE_MIN_PASSWORD - 1))
        .newPassword(StringUtils.repeat('n', UserDto.SIZE_MIN_PASSWORD - 1))
        .build();

    // when
    ResultActions resultActions = post(URL, userDto);

    // then
    resultActions
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").value(ApiErrorCode.COMMON_INVALID_BODY.code()))
        .andExpect(jsonPath("$.message").value(ApiErrorCode.COMMON_INVALID_BODY.message()))
        .andExpect(jsonPath("$.details", containsInAnyOrder(
            allOf(
                hasEntry("name", "username"),
                hasEntry("value", userDto.getUsername()),
                hasEntry("reason", String.format("size must be between %d and %d",
                    UserDto.SIZE_MIN_USERNAME, UserDto.SIZE_MAX_USERNAME))),
            allOf(
                hasEntry("name", "password"),
                hasEntry("value", userDto.getPassword()),
                hasEntry("reason", String.format("size must be between %d and %d",
                    UserDto.SIZE_MIN_PASSWORD, UserDto.SIZE_MAX_PASSWORD))),
            allOf(
                hasEntry("name", "newPassword"),
                hasEntry("value", userDto.getNewPassword()),
                hasEntry("reason", String.format("size must be between %d and %d",
                    UserDto.SIZE_MIN_PASSWORD, UserDto.SIZE_MAX_PASSWORD)))
            ))).andDo(print());
  }

  @Test
  public void invalidSizeMax() throws Exception {
    // given
    UserDto userDto = userDtoBuilder
        .username(StringUtils.repeat('u', UserDto.SIZE_MAX_USERNAME + 1))
        .password(StringUtils.repeat('p', UserDto.SIZE_MAX_PASSWORD + 1))
        .newPassword(StringUtils.repeat('n', UserDto.SIZE_MAX_PASSWORD + 1))
        .build();

    // when
    ResultActions resultActions = post(URL, userDto);

    // then
    resultActions
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").value(ApiErrorCode.COMMON_INVALID_BODY.code()))
        .andExpect(jsonPath("$.message").value(ApiErrorCode.COMMON_INVALID_BODY.message()))
        .andExpect(jsonPath("$.details", containsInAnyOrder(
            allOf(
                hasEntry("name", "username"),
                hasEntry("value", userDto.getUsername()),
                hasEntry("reason", String.format("size must be between %d and %d",
                    UserDto.SIZE_MIN_USERNAME, UserDto.SIZE_MAX_USERNAME))),
            allOf(
                hasEntry("name", "password"),
                hasEntry("value", userDto.getPassword()),
                hasEntry("reason", String.format("size must be between %d and %d",
                    UserDto.SIZE_MIN_PASSWORD, UserDto.SIZE_MAX_PASSWORD))),
            allOf(
                hasEntry("name", "newPassword"),
                hasEntry("value", userDto.getNewPassword()),
                hasEntry("reason", String.format("size must be between %d and %d",
                    UserDto.SIZE_MIN_PASSWORD, UserDto.SIZE_MAX_PASSWORD)))
        ))).andDo(print());
  }

  @Test
  public void invalidEmail() throws Exception {
    // given
    UserDto userDto = userDtoBuilder
        .email("e")
        .build();

    // when
    ResultActions resultActions = post(URL, userDto);

    // then
    resultActions
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").value(ApiErrorCode.COMMON_INVALID_BODY.code()))
        .andExpect(jsonPath("$.message").value(ApiErrorCode.COMMON_INVALID_BODY.message()))
        .andExpect(jsonPath("$.details", containsInAnyOrder(
            allOf(
                hasEntry("name", "email"),
                hasEntry("value", userDto.getEmail()),
                hasEntry("reason", "must be a well-formed email address")))
        )).andDo(print());
  }

  @Test
  public void invalidNotEmpty() throws Exception {
    // given
    UserDto userDto = userDtoBuilder
        .username(null)
        .email(null)
        .password(null)
        .newPassword(null)
        .build();

    // when
    ResultActions resultActions = post(URL, userDto);

    // then
    resultActions
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").value(ApiErrorCode.COMMON_INVALID_BODY.code()))
        .andExpect(jsonPath("$.message").value(ApiErrorCode.COMMON_INVALID_BODY.message()))
        .andExpect(jsonPath("$.details", containsInAnyOrder(
            allOf(
                hasEntry("name", "username"),
                hasEntry("value", StringUtils.EMPTY),
                hasEntry("reason", "must not be empty")
            ),
            allOf(
                hasEntry("name", "email"),
                hasEntry("value", StringUtils.EMPTY),
                hasEntry("reason", "must not be empty")
            ),
            allOf(
                hasEntry("name", "password"),
                hasEntry("value", StringUtils.EMPTY),
                hasEntry("reason", "must not be empty")
            ),
            allOf(
                hasEntry("name", "newPassword"),
                hasEntry("value", StringUtils.EMPTY),
                hasEntry("reason", "must not be empty")
            )
        ))).andDo(print());
  }

  @Test
  public void create() throws Exception {
    // given
    UserDto userDto = userDtoBuilder.build();

    given(userService.create(userDto)).willReturn(userDto);

    // when
    ResultActions resultActions = post(URL, userDto);

    // then
    resultActions
        .andExpect(status().isCreated())
        .andDo(print());
  }

  @Test
  public void update() throws Exception {
    // given
    UserDto userDto = userDtoBuilder.build();

    given(userService.update(userDto.getId(), userDto)).willReturn(userDto);

    // when
    ResultActions resultActions = put(URL + "/" + userDto.getId(), userDto);

    // then
    resultActions
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  public void get() throws Exception {
    // given
    UserDto userDto = userDtoBuilder.build();

    given(userService.get(userDto.getId())).willReturn(userDto);

    // when
    ResultActions resultActions = get(URL + "/" + userDto.getId());

    // then
    resultActions
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  public void getAll() {
    // TODO: test
  }

  @Test
  public void delete() throws Exception {
    // given
    UserDto userDto = userDtoBuilder.build();

    // when
    ResultActions resultActions = delete(URL + "/" + userDto.getId());

    // then
    resultActions
        .andExpect(status().isOk())
        .andDo(print());
  }

}

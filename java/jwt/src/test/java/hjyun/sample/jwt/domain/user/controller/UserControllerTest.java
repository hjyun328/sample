package hjyun.sample.jwt.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hjyun.sample.jwt.domain.user.dto.UserDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;

// TODO: more test
@SpringBootTest
@RunWith(SpringRunner.class)
//@WebMvcTest(UserController.class) // Controller, ControllerAdvice만 자동 설정됨
@AutoConfigureMockMvc  // Controller, Service, Resource, Repository, Component 모든 컨텍스트를 함께 올림
@Transactional
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void create() throws Exception {
    // given
    final UserDto userDto = UserDto.builder()
        .username("foo")
        .password("foo")
        .email("foo@bar.com")
        .build();

    // when
    final ResultActions resultActions = mockMvc.perform(
        post("/api/v1/users")
            .content(objectMapper.writeValueAsString(userDto))
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print());

    // then
    resultActions
        .andExpect(status().isCreated())
        .andDo(print())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

}

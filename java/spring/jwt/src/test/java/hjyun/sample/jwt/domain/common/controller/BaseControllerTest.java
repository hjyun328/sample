package hjyun.sample.jwt.domain.common.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hjyun.sample.jwt.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

public class BaseControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  protected UserRepository userRepository;

  protected ResultActions post(String url, String content) throws Exception {
    return mockMvc.perform(
      MockMvcRequestBuilders.post(url)
        .content(content)
        .contentType(MediaType.APPLICATION_JSON))
        .andDo(print());
  }

  protected ResultActions post(String url, Object content) throws Exception {
    return post(url, objectMapper.writeValueAsString(content));
  }

  protected ResultActions put(String url, String content) throws Exception {
    return mockMvc.perform(
        MockMvcRequestBuilders.put(url)
          .content(content)
          .contentType(MediaType.APPLICATION_JSON))
          .andDo(print());
  }

  protected ResultActions put(String url, Object content) throws Exception {
    return put(url, objectMapper.writeValueAsString(content));
  }

  protected ResultActions get(String url) throws Exception {
    return mockMvc.perform(
        MockMvcRequestBuilders.get(url))
        .andDo(print());
  }

  protected ResultActions delete(String url) throws Exception {
    return mockMvc.perform(
        MockMvcRequestBuilders.delete(url))
        .andDo(print());
  }

}

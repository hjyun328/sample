package hjyun.sample.jwt.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfiguration {

  @Bean
  public ObjectMapper objectMapper() {
    Hibernate5Module hibernateModule = new Hibernate5Module();

    hibernateModule.disable(Hibernate5Module.Feature.FORCE_LAZY_LOADING);

    return Jackson2ObjectMapperBuilder
        .json()
        .featuresToDisable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)
        .modules(new JavaTimeModule(), hibernateModule)
        .serializationInclusion(JsonInclude.Include.NON_EMPTY)
        .build();
  }

}

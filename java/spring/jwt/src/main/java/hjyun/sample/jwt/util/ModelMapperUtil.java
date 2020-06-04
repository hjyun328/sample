package hjyun.sample.jwt.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ModelMapperUtil {

  private static final ModelMapper modelMapper;

  public static <D> D map(Object object, Class<D> destinationType) {
    return modelMapper.map(object, destinationType);
  }

  static {
    modelMapper = initializeModelMapper();
  }

  private static ModelMapper initializeModelMapper() {
    ModelMapper modelMapper = new ModelMapper();

    Configuration configuration = modelMapper.getConfiguration();
    configuration.setMatchingStrategy(MatchingStrategies.STRICT);
    configuration.setFieldMatchingEnabled(true);
    configuration.setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);

    return modelMapper;
  }

}

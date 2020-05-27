package hjyun.sample.jwt.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Getter
public class ApiError {

  @JsonIgnore
  private final HttpStatus status;
  private final String message;
  private final String code;
  private final Collection<Detail> details;

  private ApiError(HttpStatus status,
                  String message,
                  String code,
                  Collection<Detail> details) {
    this.status = status;
    this.message = message;
    this.code = code;
    this.details = details;
  }

  private ApiError(ApiErrorCode apiErrorCode, Collection<Detail> details) {
    this(apiErrorCode.status(), apiErrorCode.message(), apiErrorCode.code(), details);
  }

  private ApiError(ApiErrorCode apiErrorCode, Detail detail) {
    this(apiErrorCode.status(), apiErrorCode.message(), apiErrorCode.code(), List.of(detail));
  }

  public static ApiError of(ApiErrorCode apiErrorCode) {
    return new ApiError(apiErrorCode.status(), apiErrorCode.message(), apiErrorCode.code(),
        Collections.emptyList());
  }

  public static ApiError of(ApiErrorCode apiErrorCode, InvalidFormatException exception) {
    final List<JsonMappingException.Reference> references = exception.getPath();

    if (references.size() > 0) {
      return new ApiError(apiErrorCode,
          List.of(new Detail(
              references.get(0).getFieldName(),
              Objects.toString(exception.getValue(), StringUtils.EMPTY),
              String.format(
                  ApiErrorCode.COMMON_INVALID_TYPE.message(),
                  exception.getTargetType().getSimpleName()))));
    }

    return of(apiErrorCode);
  }

  public static ApiError of(ApiErrorCode apiErrorCode, BindingResult bindingResult) {
    final Collection<org.springframework.validation.FieldError> fieldErrors =
        bindingResult.getFieldErrors();

    if (fieldErrors.isEmpty()) {
      return of(apiErrorCode);
    }

    return new ApiError(apiErrorCode, (Collection<Detail>) fieldErrors.stream().collect(
        ArrayList<Detail>::new,
        (details, fieldError) -> {
          Object rejectedValue = fieldError.getRejectedValue();
          details.add(
              new Detail(
                  fieldError.getField(),
                  Objects.toString(rejectedValue, StringUtils.EMPTY),
                  // FIXME: use message source & property
                  fieldError.getDefaultMessage()));
        },
        List::addAll));
  }

  public static ApiError of(ApiErrorCode apiErrorCode,
                            MethodArgumentTypeMismatchException exception) {
    final Optional<Class<?>> optionalClass = Optional.ofNullable(exception.getRequiredType());

    return new ApiError(apiErrorCode, new Detail(
        exception.getName(),
        Objects.toString(exception.getValue(), StringUtils.EMPTY),
        optionalClass.map(c ->
            String.format(ApiErrorCode.COMMON_INVALID_TYPE.message(), c.getSimpleName()))
            .orElse(StringUtils.EMPTY)));
  }

  public static void response(HttpServletResponse response,
                              ApiError apiError,
                              ObjectMapper mapper)
      throws IOException {
    response.setStatus(apiError.getStatus().value());
    response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write(mapper.writeValueAsString(apiError));
    response.getWriter().flush();
    response.getWriter().close();
  }

  @Getter
  public static class Detail {

    private final String name;
    private final String value;
    private final String reason;

    private Detail(String name, String value, String reason) {
      this.name = name;
      this.value = value;
      this.reason = reason;
    }

    public static Detail of(String name, String value, String reason) {
      return new Detail(name, value, reason);
    }

  }

}

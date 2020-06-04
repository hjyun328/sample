package hjyun.sample.jwt.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class ApiErrorTest {

  @Test
  public void of() {
    // given
    ApiErrorCode apiErrorCode = ApiErrorCode.COMMON_INTERNAL_SERVER_ERROR;

    // when
    ApiError apiError = ApiError.of(ApiErrorCode.COMMON_INTERNAL_SERVER_ERROR);

    // then
    assertThat(apiError.getStatus(), is(apiErrorCode.status()));
    assertThat(apiError.getCode(), is(apiErrorCode.code()));
    assertThat(apiError.getMessage(), is(apiErrorCode.message()));
  }

  @Test
  public void of_withInvalidFormatException() {
    // given
    String fieldName = "foo";
    String fieldValue = "bar";
    Class<?> fieldType = String.class;

    ApiErrorCode apiErrorCode = ApiErrorCode.COMMON_INVALID_BODY;

    JsonMappingException.Reference reference = mock(JsonMappingException.Reference.class);
    given(reference.getFieldName()).willReturn(fieldName);

    InvalidFormatException exception = mock(InvalidFormatException.class);
    given(exception.getPath()).willReturn(List.of(reference));
    given(exception.getValue()).willReturn(fieldValue);
    doReturn(fieldType).when(exception).getTargetType();

    // when
    ApiError apiError = ApiError.of(apiErrorCode, exception);

    // then
    assertThat(apiError.getStatus(), is(apiErrorCode.status()));
    assertThat(apiError.getCode(), is(apiErrorCode.code()));
    assertThat(apiError.getMessage(), is(apiErrorCode.message()));
    assertThat(apiError.getDetails(), containsInAnyOrder(
        allOf(
            hasProperty("name", is(fieldName)),
            hasProperty("value", is(fieldValue)),
            hasProperty("reason", is(
                String.format(
                    ApiErrorCode.COMMON_INVALID_TYPE.message(),
                    fieldType.getSimpleName())))
        )
    ));
  }

  @Test
  public void of_withInvalidFormatException_emptyReferences() {
    // given
    ApiErrorCode apiErrorCode = ApiErrorCode.COMMON_INVALID_BODY;

    InvalidFormatException exception = mock(InvalidFormatException.class);
    given(exception.getPath()).willReturn(Collections.emptyList());

    // when
    ApiError apiError = ApiError.of(apiErrorCode, exception);

    // then
    assertThat(apiError.getStatus(), is(apiErrorCode.status()));
    assertThat(apiError.getCode(), is(apiErrorCode.code()));
    assertThat(apiError.getMessage(), is(apiErrorCode.message()));
    assertThat(apiError.getDetails(), is(empty()));
  }

  @Test
  public void of_withBindingResult() {
    // given
    ApiErrorCode apiErrorCode = ApiErrorCode.COMMON_INVALID_BODY;

    List<FieldError> fieldErrors = List.of(
        new FieldError("foo", "fooField", "fooValue", false, null, null, "fooMessage"),
        new FieldError("bar", "barField", "barValue", false, null, null, "barMessage")
    );

    BindingResult bindingResult = mock(BindingResult.class);
    given(bindingResult.getFieldErrors()).willReturn(fieldErrors);

    // when
    ApiError apiError = ApiError.of(apiErrorCode, bindingResult);

    // then
    assertThat(apiError.getStatus(), is(apiErrorCode.status()));
    assertThat(apiError.getCode(), is(apiErrorCode.code()));
    assertThat(apiError.getMessage(), is(apiErrorCode.message()));
    assertThat(apiError.getDetails(), containsInAnyOrder(
        allOf(
            hasProperty("name", is(fieldErrors.get(0).getField())),
            hasProperty("value", is(fieldErrors.get(0).getRejectedValue())),
            hasProperty("reason", is(fieldErrors.get(0).getDefaultMessage()))
        ),
        allOf(
            hasProperty("name", is(fieldErrors.get(1).getField())),
            hasProperty("value", is(fieldErrors.get(1).getRejectedValue())),
            hasProperty("reason", is(fieldErrors.get(1).getDefaultMessage()))
        )
    ));
  }

  @Test
  public void of_withBindingResult_emptyFieldErrors() {
    // given
    ApiErrorCode apiErrorCode = ApiErrorCode.COMMON_INVALID_BODY;

    BindingResult bindingResult = mock(BindingResult.class);
    given(bindingResult.getFieldErrors()).willReturn(Collections.emptyList());

    // when
    ApiError apiError = ApiError.of(apiErrorCode, bindingResult);

    // then
    assertThat(apiError.getStatus(), is(apiErrorCode.status()));
    assertThat(apiError.getCode(), is(apiErrorCode.code()));
    assertThat(apiError.getMessage(), is(apiErrorCode.message()));
    assertThat(apiError.getDetails(), is(empty()));
  }

  @Test
  public void of_withMethodArgumentTypeMismatchException() {
    // given
    ApiErrorCode apiErrorCode = ApiErrorCode.COMMON_INVALID_PARAMETER;

    MethodArgumentTypeMismatchException exception =
        mock(MethodArgumentTypeMismatchException.class);

    given(exception.getName()).willReturn("foo");
    given(exception.getValue()).willReturn("bar");

    Class<?> requiredType = String.class;
    doReturn(requiredType).when(exception).getRequiredType();

    // when
    ApiError apiError = ApiError.of(apiErrorCode, exception);

    // then
    assertThat(apiError.getStatus(), is(apiErrorCode.status()));
    assertThat(apiError.getCode(), is(apiErrorCode.code()));
    assertThat(apiError.getMessage(), is(apiErrorCode.message()));
    assertThat(apiError.getDetails(),  containsInAnyOrder(
        allOf(
            hasProperty("name", is(exception.getName())),
            hasProperty("value", is(exception.getValue())),
            hasProperty("reason", is(String.format(
                ApiErrorCode.COMMON_INVALID_TYPE.message(), requiredType.getSimpleName())))
        )
    ));
  }

  @Test
  public void of_withMethodArgumentTypeMismatchException_nullRequiredType() {
    // given
    ApiErrorCode apiErrorCode = ApiErrorCode.COMMON_INVALID_PARAMETER;

    MethodArgumentTypeMismatchException exception =
        mock(MethodArgumentTypeMismatchException.class);

    given(exception.getName()).willReturn("foo");
    given(exception.getValue()).willReturn("bar");

    doReturn(null).when(exception).getRequiredType();

    // when
    ApiError apiError = ApiError.of(apiErrorCode, exception);

    // then
    assertThat(apiError.getStatus(), is(apiErrorCode.status()));
    assertThat(apiError.getCode(), is(apiErrorCode.code()));
    assertThat(apiError.getMessage(), is(apiErrorCode.message()));
    assertThat(apiError.getDetails(), containsInAnyOrder(
        allOf(
            hasProperty("name", is(exception.getName())),
            hasProperty("value", is(exception.getValue())),
            hasProperty("reason", is(StringUtils.EMPTY))
        )
    ));
  }

}

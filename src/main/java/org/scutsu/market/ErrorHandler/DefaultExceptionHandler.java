package org.scutsu.market.ErrorHandler;

import lombok.Data;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;
import java.util.stream.Stream;

@Data
class ApiError {
	private String errorCode;
	private String message;
}

@ControllerAdvice
public class DefaultExceptionHandler {

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiError> accessDeniedHandler() {
		ApiError apiError = new ApiError();
		apiError.setErrorCode("access-denied");
		apiError.setMessage("拒绝访问");
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
	}

	@ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
	public ResponseEntity<ApiError> methodArgumentNotValidHandler(Exception e) {
		ApiError apiError = new ApiError();
		apiError.setErrorCode("bad-request");
		apiError.setMessage("输入参数不合法：" + e.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
	}

	@ExceptionHandler
	public ResponseEntity<ApiError> defaultErrorHandler(Throwable e) {
		ApiError apiError = new ApiError();
		apiError.setMessage(e.getMessage());
		HttpStatus httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR;
		String staticErrorCode = null;
		String dynamicErrorCode = null;

		var apiErrorCodePrefixAnnotation = AnnotationUtils.findAnnotation(e.getClass(), ApiErrorCodePrefix.class);
		if (apiErrorCodePrefixAnnotation != null) {
			staticErrorCode = apiErrorCodePrefixAnnotation.value();
		}
		if (e instanceof ApiErrorCode) {
			dynamicErrorCode = ((ApiErrorCode) e).getErrorCode();
		}
		var errorCode = Stream.of(staticErrorCode, dynamicErrorCode)
			.filter(StringUtils::hasText)
			.reduce((a, b) -> a + "." + b);
		if (errorCode.isPresent()) {
			apiError.setErrorCode(errorCode.get());
			httpStatusCode = HttpStatus.BAD_REQUEST;
		} else {
			apiError.setErrorCode("unknown");
			e.printStackTrace();
		}
		var statusAnnotation = AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class);
		if (statusAnnotation != null) {
			httpStatusCode = statusAnnotation.code();
		}

		return ResponseEntity.status(httpStatusCode).body(apiError);
	}
}

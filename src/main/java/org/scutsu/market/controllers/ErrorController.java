package org.scutsu.market.controllers;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

/**
 * Error from {@code response.sendError(...)} will be routed here
 */
@RestController
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

	private final ErrorAttributes errorAttributes;
	private final ErrorProperties errorProperties;

	public ErrorController(ErrorAttributes errorAttributes, ServerProperties serverProperties) {
		this.errorAttributes = errorAttributes;
		this.errorProperties = serverProperties.getError();
	}

	@RequestMapping
	public ResponseEntity errorRest(WebRequest request) throws Throwable {
		Throwable e = errorAttributes.getError(request);
		if (e != null) {
			// Rethrow exception so that ControllerAdvice can handle this.
			throw e;
		}
		ApiError apiError = new ApiError();
		apiError.setErrorCode("unknown");
		var attributes = errorAttributes.getErrorAttributes(request, false);
		var message = attributes.get("message");
		apiError.setMessage(StringUtils.isEmpty(message) ? "Unknown error" : (String) message);
		Integer status = (Integer) attributes.get("status");
		if (status == null) {
			status = HttpStatus.INTERNAL_SERVER_ERROR.value();
		}
		return ResponseEntity.status(status).body(apiError);
	}

	@Override
	public String getErrorPath() {
		return errorProperties.getPath();
	}
}

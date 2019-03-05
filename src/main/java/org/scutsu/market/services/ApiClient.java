package org.scutsu.market.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.scutsu.market.ErrorHandler.ApiErrorCodePrefix;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@ApiErrorCodePrefix("we-chat.api-request")
class WeChatApiRequestException extends RuntimeException {
	WeChatApiRequestException(String message) {
		super(message);
	}

	WeChatApiRequestException(Throwable e) {
		super(e);
	}
}

@RequiredArgsConstructor
@Component
public class ApiClient {

	private final ObjectMapper objectMapper;

	/**
	 * @throws WeChatApiRequestException Request failed
	 */
	public <T> T get(URI uri, Class<T> responseType) {
		HttpRequest request = HttpRequest.newBuilder()
			.uri(uri)
			.build();
		return request(request, responseType);
	}

	/**
	 * @throws WeChatApiRequestException Request failed
	 */
	public <T> T post(URI uri, Object body, Class<T> responseType) {
		try {
			byte[] bodyBytes = objectMapper.writeValueAsBytes(body);
			HttpRequest request = HttpRequest.newBuilder()
				.uri(uri)
				.POST(HttpRequest.BodyPublishers.ofByteArray(bodyBytes))
				.build();
			return request(request, responseType);
		} catch (JsonProcessingException e) {
			throw new WeChatApiRequestException(e);
		}
	}

	/**
	 * @throws WeChatApiRequestException Request failed
	 */
	public <T> T request(HttpRequest request, Class<T> responseType) {
		HttpClient client = HttpClient.newHttpClient();
		try {
			var response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

			if (response.statusCode() != HttpStatus.OK.value()) {
				throw new WeChatApiRequestException(request.uri().toString() + " returned unexpected http status code " + response.statusCode());
			}

			return objectMapper.readValue(response.body(), responseType);

		} catch (IOException | InterruptedException e) {
			throw new WeChatApiRequestException(e);
		}
	}

}

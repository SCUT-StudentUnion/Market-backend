package org.scutsu.market.services;

import lombok.Data;
import org.scutsu.market.controllers.wechat.FileUploadController;
import org.scutsu.market.models.Photo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

@Data
@Component
@ConfigurationProperties(prefix = "upload")
class UploadProperties {

	/**
	 * URI that the uploaded files will be served. leave null to serve uploaded files automatically.
	 */
	private URI uri;
}

@Component
public class PhotoUriGenerator {

	private final UploadProperties uploadProperties;

	@Autowired
	public PhotoUriGenerator(UploadProperties uploadProperties) {
		this.uploadProperties = uploadProperties;
	}

	public URI generateUri(Photo photo) {

		URI baseUri = uploadProperties.getUri();
		if (baseUri == null) {
			try {
				var link = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(FileUploadController.class).serveFile(photo.getFileName()));
				return link.toUri();
			} catch (IOException e) {
				throw new RuntimeException(e); //Should never happen
			}
		} else {
			return baseUri.resolve(photo.getFileName());
		}
	}
}

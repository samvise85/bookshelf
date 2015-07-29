package it.samvise85.bookshelf.web.config;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.TransformedResource;

public abstract class BookshelfTransformer {

	public abstract Resource transform(HttpServletRequest request, Resource resource) throws IOException;
	
	protected String getResourceContent(Resource resource) throws IOException {
		if(resource instanceof TransformedResource) {
			return new String(((TransformedResource)resource).getByteArray());
		} else {
			return FileUtils.readFileToString(resource.getFile());
		}
	}
}

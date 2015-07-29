package it.samvise85.bookshelf.web.config;

import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.TransformedResource;

public class VersionTransformer extends BookshelfTransformer {
	@Override
	public Resource transform(HttpServletRequest request, Resource resource) throws IOException {
		String fileContent = getResourceContent(resource);
		
		StringBuffer sb = new StringBuffer();
		Pattern pattern = Pattern.compile("\\[\\[VERSION\\]\\]");
		Matcher matcher = pattern.matcher(fileContent);
		String replacement = "?ver=" + new Date().getTime();
		while(matcher.find())
			matcher.appendReplacement(sb, replacement);
		matcher.appendTail(sb);
		
		TransformedResource transformedResource = new TransformedResource(resource, sb.toString().getBytes());
		return transformedResource;
	}
}

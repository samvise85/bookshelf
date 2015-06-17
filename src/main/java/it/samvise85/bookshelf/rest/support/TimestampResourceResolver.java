package it.samvise85.bookshelf.rest.support;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

public class TimestampResourceResolver extends PathResourceResolver {

	@Override
	protected Resource resolveResourceInternal(HttpServletRequest request, String requestPath, List<? extends Resource> locations,
			ResourceResolverChain chain) {
		String path = requestPath;
		Pattern pattern = Pattern.compile("_[0-9]+");
		Matcher matcher = pattern.matcher(path);
		path = matcher.replaceFirst("");
		Resource resource = chain.resolveResource(request, path, locations);
		return resource;
	}
}

package it.samvise85.bookshelf.web.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;

public class BookshelfTransformerChain {
	List<BookshelfTransformer> chain = new ArrayList<BookshelfTransformer>();

	public BookshelfTransformerChain add(BookshelfTransformer e) {
		chain.add(e);
		return this;
	}

	public Resource transform(HttpServletRequest request, Resource resource) throws IOException {
		if(chain != null) {
			for(BookshelfTransformer t : chain) {
				resource = t.transform(request, resource);
			}
		}
		return resource;
	}
}

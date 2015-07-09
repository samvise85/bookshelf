package it.samvise85.bookshelf.tests;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class PatternTests {

	@Test
	public void testGetServer() {
		String requestUrl = "http://localhost:8080/bookshelf/users";
		Pattern p = Pattern.compile("(http[s]?://[^/]+(/bookshelf)?).*");
		Matcher m = p.matcher(requestUrl);
		System.out.println(m.matches());
		System.out.println(m.start(1));
		System.out.println(m.end(1));
		System.out.println(requestUrl.substring(m.start(1), m.end(1)));
		

		while(m.find()) {
			String requestApp = m.group(1);
			Assert.assertNotNull(requestApp);
		}
	}
}

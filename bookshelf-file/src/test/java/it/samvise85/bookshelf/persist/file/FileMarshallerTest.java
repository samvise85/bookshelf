package it.samvise85.bookshelf.persist.file;

import it.samvise85.bookshelf.model.user.User;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FileMarshallerTest {
	private User exampleUser;
	private String exampleUserJson;

	@Before
	public void init() {
		exampleUser = new User("samvise85", "samvise85@balbla.com");
		exampleUser.setId("samvise85");
		
		exampleUserJson = "{\"id\":\"samvise85\",\"username\":\"samvise85\",\"email\":\"samvise85@balbla.com\",\"password\":null,\"admin\":false,\"name\":null,\"surname\":null,"
		+ "\"country\":null,\"language\":null,\"birthYear\":null,\"birthday\":null,\"activationCode\":null,\"resetCode\":null,"
		+ "\"creation\":null,\"lastModification\":null}";
	}
	
	@Test
	public void testMarshall() {
		User u = exampleUser;
		
		String json = FileMarshaller.marshall(u);
		
		String check = exampleUserJson;
		Assert.assertEquals(check, json);
	}
	
	@Test
	public void testUnmarshall() {
		User u = exampleUser;
		
		String json = exampleUserJson;
		
		User user = FileMarshaller.unmarshall(User.class, json);
		
		Assert.assertEquals(u, user);
	}
}

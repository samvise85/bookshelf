package it.samvise85.bookshelf.persist.file;

import it.samvise85.bookshelf.model.user.User;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class FileRetrieverTest {
	private static User exampleUser;
	private static String exampleUserJson;
	private static User updatedUser;
	private static String updatedUserJson;

	@BeforeClass
	public static void init() {
		exampleUser = new User("samvise84", "samvise85@balbla.com");
		exampleUser.setId(exampleUser.getUsername());
		updatedUser = new User(exampleUser.getUsername(), exampleUser.getEmail());
		updatedUser.setId(exampleUser.getId());
		updatedUser.setFirstname("Luca");
		updatedUser.setLastname("Piazza");
		updatedUser.setAdmin(true);
		
		exampleUserJson = "{\"id\":\"samvise85\",\"username\":\"samvise85\",\"email\":\"samvise85@balbla.com\",\"password\":null,\"admin\":false,\"name\":null,\"surname\":null,"
		+ "\"country\":null,\"language\":null,\"birthYear\":null,\"birthday\":null,\"activationCode\":null,\"resetCode\":null,"
		+ "\"creation\":null,\"lastModification\":null}";
		updatedUserJson = "{\"id\":\"samvise85\",\"username\":\"samvise85\",\"email\":\"samvise85@balbla.com\",\"password\":null,\"admin\":true,\"name\":\"Luca\",\"surname\":\"Piazza\","
		+ "\"country\":null,\"language\":null,\"birthYear\":null,\"birthday\":null,\"activationCode\":null,\"resetCode\":null,"
		+ "\"creation\":null,\"lastModification\":null}";
	}
	
	@After
	public void destroy() {
		File file = FileRetriever.getFile(User.class, exampleUser.getId());
		file.delete();
	}
	
	@Test
	public void testSave() throws IOException {
		User u = exampleUser;
		
		File file = FileRetriever.save(u.getClass(), u.getId(), FileMarshaller.marshall(u));
		
		Assert.assertTrue(file.exists());
		
		String readFileToString = FileUtils.readFileToString(file);
		Assert.assertEquals(exampleUserJson, readFileToString);
	}
	
	@Test
	public void testRead() throws IOException {
		testSave();

		String readFileToString = FileRetriever.read(User.class, exampleUser.getId());
		Assert.assertEquals(exampleUserJson, readFileToString);
	}
	
	@Test
	public void testUpdate() throws IOException {
		testSave();
		
		FileRetriever.update(User.class, exampleUser.getId(), FileMarshaller.marshall(updatedUser));
		
		String string = FileRetriever.read(User.class, exampleUser.getId());
		Assert.assertEquals(updatedUserJson, string);
	}
}

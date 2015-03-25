package it.samvise85.bookshelf.persist.file;

import it.samvise85.bookshelf.model.user.User;
import it.samvise85.bookshelf.persist.PersistOptions;
import it.samvise85.bookshelf.persist.clauses.Order;
import it.samvise85.bookshelf.persist.clauses.OrderClause;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class FilePersistenceUnitTest {
	private static User user1;
	private static User user2;

	@BeforeClass
	public static void init() {
		user1 = new User("samvise85", "samvise85@balbla.com");
		user1.setId("samvise85");
		user1.setFirstname("Luca");
		user1.setLastname("Piazza");
		user2 = new User("pupazzognappo", "pupazzognappo@balbla.com");
		user2.setId("pupazzognappo");
		user2.setFirstname("Pupazzo");
		user2.setLastname("Gnappo");
		
		FileRetriever.save(User.class, user1.getId(), FileMarshaller.marshall(user1));
		FileRetriever.save(User.class, user2.getId(), FileMarshaller.marshall(user2));
	}
	
	@AfterClass
	public static void destroy() {
		FileRetriever.delete(User.class, user1.getId());
		FileRetriever.delete(User.class, user2.getId());
	}

	@Test
	public void testGetList() {
		List<User> expected = Arrays.asList(new User[] {user2, user1});
		
		FilePersistenceUnit<User> fpu = new FilePersistenceUnit<User>(User.class);
		
		List<User> list = fpu.getList(null);
		
		Assert.assertNotNull(list);
		Assert.assertEquals(expected.size(), list.size());
		for(int i = 0; i < expected.size(); i++) {
			Assert.assertEquals(expected.get(i).getId(), list.get(i).getId());
		}
	}

	@Test
	public void testGetList2() {
		List<User> expected = Arrays.asList(new User[] {user1, user2});
		
		FilePersistenceUnit<User> fpu = new FilePersistenceUnit<User>(User.class);
		
		List<User> list = fpu.getList(new PersistOptions(null, null, Collections.singletonList(new OrderClause("name", Order.ASC))));
		
		Assert.assertNotNull(list);
		Assert.assertEquals(expected.size(), list.size());
		for(int i = 0; i < expected.size(); i++) {
			Assert.assertEquals(expected.get(i).getId(), list.get(i).getId());
		}
	}

	@Test
	public void testGetList3() {
		List<User> expected = Arrays.asList(new User[] {user1, user2});
		
		FilePersistenceUnit<User> fpu = new FilePersistenceUnit<User>(User.class);
		
		List<User> list = fpu.getList(new PersistOptions(null, null, Collections.singletonList(new OrderClause("surname", Order.DESC))));
		
		Assert.assertNotNull(list);
		Assert.assertEquals(expected.size(), list.size());
		for(int i = 0; i < expected.size(); i++) {
			Assert.assertEquals(expected.get(i).getId(), list.get(i).getId());
		}
	}
}

package it.samvise85.bookshelf.web.security;

import it.samvise85.bookshelf.utils.BookshelfConstants;

public class BookshelfRole {
	public static final String UNKNOWN = BookshelfConstants.Roles.ROLE_PREFIX + "UNKNOWN";
	public static final String ANYONE = BookshelfConstants.Roles.ROLE_PREFIX + "ANYONE";
	public static final String ADMIN = BookshelfConstants.Roles.ROLE_PREFIX + "ADMIN";
	public static final String AUTHOR = BookshelfConstants.Roles.ROLE_PREFIX + "AUTHOR";
	public static final String MODERATOR = BookshelfConstants.Roles.ROLE_PREFIX + "MODERATOR";
	
	public static enum Role {
		UNKNOWN(-1, BookshelfRole.UNKNOWN),
		ANYONE(0, BookshelfRole.ANYONE), //Anyone can be ANYONE so id is 0
		ADMIN(1, BookshelfRole.ADMIN),
		AUTHOR(2, BookshelfRole.AUTHOR),
		MODERATOR(4, BookshelfRole.MODERATOR),
		;
		
		private Integer id;
		private String role;
	
		private Role(Integer id, String role) {
			this.id = id;
			this.role = role;
		}
	
		public String getRole() {
			return role;
		}
	
		public Integer getId() {
			return id;
		}
	}
}

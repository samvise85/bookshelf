package it.samvise85.bookshelf.utils;


public interface BookshelfConstants {
	
	public interface Roles {
		public static final String ROLE_PREFIX = "ROLE_";
	}
	
	public interface Pagination {
		public static final Integer DEFAULT_PAGE_SIZE = 10;
		public static final Integer SMALL_PAGE_SIZE = 5;
		public static final Integer BIG_PAGE_SIZE = 20;
		public static final Integer MAX_PAGE_SIZE = Integer.MAX_VALUE;
	}
	
	public interface Env {
		public static final String ENV_OPENSHIFT_DATA_DIR = "OPENSHIFT_DATA_DIR";
		public static final String ENV_USER_HOME = "user.home";
	}
	
	public interface Mail {
		public static final String MAIL_PROPERTIES_DIR = "mail.properties.dir";
		public static final String MAIL_PROPERTIES_FILENAME = "mail.properties";
		
		public static final String SMTP_HOST = "smtp.host";
		public static final String SMTP_PORT = "smtp.port";
		public static final String SMTP_PROTOCOL = "smtp.protocol";
		public static final String SMTP_USERNAME = "smtp.username";
		public static final String SMTP_PASSWORD = "smtp.password";
		public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
		public static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";

		public static final String FROM_ADDRESS = "mail.from.address";
		public static final String FROM_ALIAS = "mail.from.alias";
	}
	
	public interface Settings {
		public static final String APP_VERSION = "app.version";
		
		public static final String SMTP_HOST = Mail.SMTP_HOST;
		public static final String SMTP_PORT = Mail.SMTP_PORT;
		public static final String SMTP_PROTOCOL = Mail.SMTP_PROTOCOL;
		public static final String SMTP_USERNAME = Mail.SMTP_USERNAME;
		public static final String SMTP_PASSWORD = Mail.SMTP_PASSWORD;
		public static final String MAIL_SMTP_AUTH = Mail.MAIL_SMTP_AUTH;
		public static final String MAIL_SMTP_STARTTLS_ENABLE = Mail.MAIL_SMTP_STARTTLS_ENABLE;
		
		public static final String FROM_ADDRESS = Mail.FROM_ADDRESS;
		public static final String FROM_ALIAS = Mail.FROM_ALIAS;
	}
}

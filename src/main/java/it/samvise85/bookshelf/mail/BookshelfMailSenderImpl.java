package it.samvise85.bookshelf.mail;

import it.samvise85.bookshelf.exception.BookshelfException;
import it.samvise85.bookshelf.manager.SettingManager;
import it.samvise85.bookshelf.model.User;
import it.samvise85.bookshelf.utils.BookshelfConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
public class BookshelfMailSenderImpl implements BookshelfMailSender {
	private static final Logger log = Logger.getLogger(BookshelfMailSenderImpl.class);

	private static String SIGNATURE = "<br/>Best regard<br/><i>Bookshelf team</i>";

	@Autowired
	private SettingManager settingManager;
	
	private JavaMailSenderImpl mailSender;
	private String requestApp;
	private Address from;
	

	public BookshelfMailSenderImpl() {}
	
	@PostConstruct
	private void init() {
		initFromSettings();
		if(mailSender == null)
			initFromFile();
	}

	private void initFromSettings() {
		String host = settingManager.getSetting(BookshelfConstants.Settings.SMTP_HOST);
		if(host != null) {
			StringBuilder sb = new StringBuilder("Cannot instantiate Mail Sender. Please configure ");
			boolean ok = true;

			String port = settingManager.getSetting(BookshelfConstants.Settings.SMTP_PORT);
			if(port == null) {
				sb.append(BookshelfConstants.Settings.SMTP_PORT);
				ok = false;
			}
			String protocol = settingManager.getSetting(BookshelfConstants.Settings.SMTP_PROTOCOL);
			if(protocol == null) {
				if(ok) ok = false;
				else sb.append(", ");
				sb.append(BookshelfConstants.Settings.SMTP_PROTOCOL);
			}
			
			JavaMailSenderImpl sender = new JavaMailSenderImpl();
			sender.setHost(host);
			sender.setPort(Integer.parseInt(port));
			sender.setProtocol(protocol);
			
			String mailSmtpAuth = settingManager.getSetting(BookshelfConstants.Settings.MAIL_SMTP_AUTH);
			boolean auth = mailSmtpAuth == null ? true : Boolean.parseBoolean(mailSmtpAuth);
			if(auth) {
				String username = settingManager.getSetting(BookshelfConstants.Settings.SMTP_USERNAME);
				if(username == null) {
					if(ok) ok = false;
					else sb.append(", ");
					sb.append(BookshelfConstants.Settings.SMTP_USERNAME);
				}
				String password = settingManager.getSetting(BookshelfConstants.Settings.SMTP_PASSWORD);
				if(password == null) {
					if(ok) ok = false;
					else sb.append(", ");
					sb.append(BookshelfConstants.Settings.SMTP_PASSWORD);
				}
				sender.setUsername(username);
				sender.setPassword(password);
			}
			
			if(!ok) {//there was some missing configuration
				sb.append(" settings");
				throw new BookshelfException(sb.toString());
			}
			
			Properties javaMailProps = new Properties();

			javaMailProps.put(BookshelfConstants.Settings.MAIL_SMTP_AUTH, auth);
			String mailSmtpStarttlsEnable = settingManager.getSetting(BookshelfConstants.Settings.MAIL_SMTP_STARTTLS_ENABLE);
			boolean startTls = mailSmtpStarttlsEnable == null ? true : Boolean.parseBoolean(mailSmtpStarttlsEnable);
			javaMailProps.put(BookshelfConstants.Settings.MAIL_SMTP_STARTTLS_ENABLE, startTls);
			
			sender.setJavaMailProperties(javaMailProps);
			mailSender = sender;
			
			try {
				String defFromAddress = "no-reply@bookshelf-fake.com";
				String defFromAlias = "Bookshelf no-reply";
				from = new InternetAddress(settingManager.getSettingOrDefault(BookshelfConstants.Settings.FROM_ADDRESS, defFromAddress), 
						settingManager.getSettingOrDefault(BookshelfConstants.Settings.FROM_ALIAS, defFromAlias));
			} catch(UnsupportedEncodingException e) {
				throw new BookshelfException(e.getMessage(), e);
			}
		}
	}

	private void initFromFile() {
		String dataDir = settingManager.getSetting(BookshelfConstants.Mail.MAIL_PROPERTIES_DIR);
		if(dataDir == null)
			dataDir = System.getenv(BookshelfConstants.Env.ENV_OPENSHIFT_DATA_DIR);
		if(dataDir == null)
			dataDir = System.getProperty(BookshelfConstants.Env.ENV_USER_HOME) + File.separator;
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(dataDir + BookshelfConstants.Mail.MAIL_PROPERTIES_FILENAME));
			mailSender = new JavaMailSenderImpl();
			mailSender.setHost(props.getProperty(BookshelfConstants.Mail.SMTP_HOST));
			mailSender.setPort(Integer.parseInt(props.getProperty(BookshelfConstants.Mail.SMTP_PORT)));
			mailSender.setProtocol(props.getProperty(BookshelfConstants.Mail.SMTP_PROTOCOL));
			mailSender.setUsername(props.getProperty(BookshelfConstants.Mail.SMTP_USERNAME));
			mailSender.setPassword(props.getProperty(BookshelfConstants.Mail.SMTP_PASSWORD));
			Properties javaMailProps = new Properties();
			javaMailProps.put(BookshelfConstants.Mail.MAIL_SMTP_AUTH, true);
			javaMailProps.put(BookshelfConstants.Mail.MAIL_SMTP_STARTTLS_ENABLE, true);
			mailSender.setJavaMailProperties(javaMailProps);
			
			try {
				String defFromAddress = "no-reply@bookshelf-fake.com";
				String defFromAlias = "Bookshelf no-reply";
				from = new InternetAddress(props.getProperty(BookshelfConstants.Settings.FROM_ADDRESS, defFromAddress), 
						props.getProperty(BookshelfConstants.Settings.FROM_ALIAS, defFromAlias));
			} catch(UnsupportedEncodingException e) {
				throw new BookshelfException(e.getMessage(), e);
			}
		} catch(FileNotFoundException e) {
			mailSender = null;
		} catch(IOException e) {
			throw new BookshelfException(e.getMessage(), e);
		}
	}
	
	@Override
	public void setRequestApp(String requestApp) {
		this.requestApp = requestApp;
	}
	
	@Override
	public void sendSubscriptionMail(User user) {
		MimeMessage mmessage = new MimeMessage(mailSender.getSession());
		try {
			mmessage.setFrom(from);
	        mmessage.setRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
	        mmessage.setSubject("Bookshelf - Thanks for subscription");
	        mmessage.setContent("Thanks for Subscription!<br/>Activate your account by clicking this link (or copy and paste in a browser)<br/>"
	        		+ "<a href=\"" + requestApp + "/#user/" + user.getId() + "/activate/" + user.getActivationCode() + "\">"
        			+ requestApp + "/#user/" + user.getId() + "/activate/" + user.getActivationCode()
        			+ "</a>" + SIGNATURE, "text/html");
	        log.debug("Sending activation mail to " + user.getEmail());
	        mailSender.send(mmessage);
		} catch(MessagingException e) {
			throw new BookshelfException(e.getMessage(), e);
		}
	}

	@Override
	public void sendResetMail(User user) {
		MimeMessage mmessage = new MimeMessage(mailSender.getSession());
		try {
			mmessage.setFrom(from);
	        mmessage.setRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));
	        mmessage.setSubject("Bookshelf - Reset Password");
	        mmessage.setContent("Hello!<br/>You asked to reset your password on Bookshelf. Please follow the link to confirm it is you who made the request.<br/>"
	        		+ "<a href=\"" + requestApp + "/#reset/" + user.getResetCode() + "\">"
        			+ requestApp + "/#reset/" + user.getResetCode()
        			+ "</a>" + SIGNATURE, "text/html");
	        log.debug("Sending reset mail to " + user.getEmail());
	        mailSender.send(mmessage);
		} catch(MessagingException e) {
			throw new BookshelfException(e.getMessage(), e);
		}
	}
}

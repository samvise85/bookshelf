package it.samvise85.bookshelf.mail;

import it.samvise85.bookshelf.exception.BookshelfException;
import it.samvise85.bookshelf.model.user.User;

import java.io.UnsupportedEncodingException;

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

	private static Address STANDARD_FROM;
	private static String SIGNATURE = "<br/>Best regard<br/><i>Bookshelf team</i>";
	static {
		try {
			STANDARD_FROM = new InternetAddress("no-reply@bookshelf-samvise85.rhcloud.com", "Bookshelf no-reply");
		} catch(UnsupportedEncodingException e) {
			throw new BookshelfException(e.getMessage(), e);
		}
	}
	@Autowired
	private JavaMailSenderImpl mailSender;

	private String requestApp;

	@Override
	public void setRequestApp(String requestApp) {
		this.requestApp = requestApp;
	}
	
	@Override
	public void sendSubscriptionMail(User user) {
		MimeMessage mmessage = new MimeMessage(mailSender.getSession());
		try {
			mmessage.setFrom(STANDARD_FROM);
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
			mmessage.setFrom(STANDARD_FROM);
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

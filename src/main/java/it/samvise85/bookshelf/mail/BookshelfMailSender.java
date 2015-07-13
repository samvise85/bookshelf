package it.samvise85.bookshelf.mail;

import it.samvise85.bookshelf.model.user.User;

public interface BookshelfMailSender {

	void setRequestApp(String requestApp);
	
	void sendSubscriptionMail(User newuser);

	void sendResetMail(User user);
}

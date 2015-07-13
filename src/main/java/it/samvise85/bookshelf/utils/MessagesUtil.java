package it.samvise85.bookshelf.utils;

import java.util.Locale;

import org.springframework.context.support.ResourceBundleMessageSource;

public class MessagesUtil {

	private static ResourceBundleMessageSource bundle;
	
	static {
		bundle = new ResourceBundleMessageSource();
		bundle.setBasename("i18n.messages");
	}

	public static String getMessageOrNull(String key) {
		return getMessageOrNull(key, getDefaulBundleLanguage());
	}
	
	public static String getMessageOrNull(String key, Locale locale) {
		try {
			return bundle.getMessage(key, null, locale);
		} catch(Exception e) {
			return null;
		}
	}

	public static String getMessageOrError(String key) {
		return getMessageOrError(key, getDefaulBundleLanguage());
	}

	public static Locale getDefaulBundleLanguage() {
		return Locale.ENGLISH;
	}
	
	public static String getMessageOrError(String key, Locale locale) {
		try {
			return bundle.getMessage(key, null, locale);
		} catch(Exception e) {
			return "??" + key + "??";
		}
	}
	
}

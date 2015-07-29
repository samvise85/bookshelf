package it.samvise85.bookshelf.utils;

import it.samvise85.bookshelf.context.ServiceLocator;
import it.samvise85.bookshelf.manager.LabelManager;
import it.samvise85.bookshelf.manager.LanguageManager;
import it.samvise85.bookshelf.model.Label;

import java.util.Locale;

import org.springframework.context.support.ResourceBundleMessageSource;

public class MessagesUtil {

	private static ResourceBundleMessageSource bundle;

	private static LabelManager labelManager;
	private static LanguageManager languageManager;
	
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

	public static String getLabel(String language, String key) {
		Label label = getLabelManager().get(key, language);
		String replacement = null;
		if(label != null)
			replacement = label.getLabel();
		else if(isSupported(language)) {
			//create automatically a label entry
			label = new Label();
			label.setId(key + "_" + language);
			label.setLabelKey(key);
			label.setLang(language);
			getLabelManager().create(label);
		}
		if(replacement == null)
			replacement = getMessageOrError(key);
		return replacement;
	}
	
	private static boolean isSupported(String language) {
		try {
			return getLanguageManager().get(language) != null;
		} catch(Exception e) {return false;}
	}
	
	private static LabelManager getLabelManager() {
		if(labelManager == null)
			labelManager = ServiceLocator.getInstance().getService(LabelManager.class);
		return labelManager;
	}
	
	private static LanguageManager getLanguageManager() {
		if(languageManager == null)
			languageManager = ServiceLocator.getInstance().getService(LanguageManager.class);
		return languageManager;
	}
}

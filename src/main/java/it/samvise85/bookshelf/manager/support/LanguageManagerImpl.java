package it.samvise85.bookshelf.manager.support;

import it.samvise85.bookshelf.exception.BookshelfException;
import it.samvise85.bookshelf.model.locale.Language;
import it.samvise85.bookshelf.persist.inmemory.InMemoryPersistenceUnit;
import it.samvise85.bookshelf.persist.inmemory.support.LanguageRepository;

import java.io.Serializable;
import java.util.Locale;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LanguageManagerImpl extends InMemoryPersistenceUnit<Language> implements LanguageManager {
	private static final Logger log = Logger.getLogger(LanguageManagerImpl.class);
	private static final String DEFAULT_LANGUAGE = Locale.ENGLISH.getLanguage();
	
	@Autowired
	protected LanguageRepository repository;
	
	public LanguageManagerImpl() {
		super(Language.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public LanguageRepository getRepository() {
		return repository;
	}

	@Override
	public Language getDefault() {
		Language res = null;
		try {
			res = repository.findOneByDef(Boolean.TRUE);
		} catch(Exception e) {}
		if(res == null) {
			log.warn("Default language not found. Adding " + DEFAULT_LANGUAGE + " as default.");
			res = createDefault(DEFAULT_LANGUAGE);
		}
		return res;
	}

	@Override
	public Language changeDefault(String language) {
		if(Locale.forLanguageTag(language) == null)
			throw new BookshelfException("Language " + language + " isn't a valid language.");
		
		Language def = getDefault();
				Language newdef = null;
		try {
			newdef = repository.findOne(language);
		} catch(Exception e) {}
		if(newdef == null) {
			log.warn("Language " + language + " not found. Adding " + language + " as default.");
			newdef = createDefault(language);
		}
		def.setDef(Boolean.FALSE);
		repository.save(def);
		return newdef;
	}

	private Language createDefault(String language) {
		Language def = new Language();
		def.setId(language);
		def.setDef(Boolean.TRUE);
		def.setVersion(0L);
		repository.save(def);
		return def;
	}

	@Override
	public Language get(Serializable id) {
		Language language = super.get(id);
		if(language == null) {
			language = new Language();
			language.setId((String)id);
			language.setVersion(0L);
			language.setDef(Boolean.FALSE);
			create(language);
		}
		return language;
	}

	@Override
	@Transactional(value=TxType.REQUIRES_NEW)
	public Language increaseVersion(String lang) {
		Language language = get(lang);
		language.setVersion(language.getVersion() + 1);
		return update(language);
	}

}

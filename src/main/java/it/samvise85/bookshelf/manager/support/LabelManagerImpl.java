package it.samvise85.bookshelf.manager.support;

import it.samvise85.bookshelf.model.locale.Label;
import it.samvise85.bookshelf.persist.inmemory.InMemoryPersistenceUnit;
import it.samvise85.bookshelf.persist.inmemory.support.LabelRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LabelManagerImpl extends InMemoryPersistenceUnit<Label> implements LabelManager {
	
	@Autowired
	protected LabelRepository repository;

	@Autowired
	protected LanguageManager languageManager;
	
	public LabelManagerImpl() {
		super(Label.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public LabelRepository getRepository() {
		return repository;
	}

	@Override
	public Label get(String key, String language) {
		return repository.findOneByKeyAndLang(key, language);
	}

	@Override
	public Label getOrDefault(String key, String language) {
		Label label = get(key, language);
		if(label == null || label.getLabel() == null)
			return get(key, languageManager.getDefault().getId());
		return label;
	}

	@Override
	public Label update(Label objectToUpdate) {
		Label label = super.update(objectToUpdate);
		languageManager.increaseVersion(label.getLang());
		return label;
	}

}

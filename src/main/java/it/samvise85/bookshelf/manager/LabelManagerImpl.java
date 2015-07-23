package it.samvise85.bookshelf.manager;

import it.samvise85.bookshelf.model.Label;
import it.samvise85.bookshelf.persist.AbstractPersistenceUnit;
import it.samvise85.bookshelf.persist.repository.LabelRepository;
import it.samvise85.bookshelf.utils.MessagesUtil;

import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LabelManagerImpl extends AbstractPersistenceUnit<Label> implements LabelManager {
	
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
		return repository.findOneByLabelKeyAndLang(key, language);
	}

	@Override
	public Label getDefault(String key) {
		Label label = new Label();
		label.setLabelKey(key);
		label.setLang(MessagesUtil.getDefaulBundleLanguage().toLanguageTag());
		label.setLabel(MessagesUtil.getMessageOrNull(key));
		return label;
	}

	@Override
	public Label update(Label objectToUpdate) {
		Label label = super.update(objectToUpdate);
		languageManager.increaseVersion(label.getLang());
		return label;
	}

	@Override
	public void deleteAll() {
		Iterable<Label> findAll = repository.findAll();
		Iterator<Label> iterator = findAll.iterator();
		while(iterator.hasNext()) {
			repository.delete(iterator.next());
		}
	}

}

package it.samvise85.bookshelf.manager.support;

import it.samvise85.bookshelf.model.locale.Label;
import it.samvise85.bookshelf.persist.PersistOptions;
import it.samvise85.bookshelf.persist.clauses.SelectionClause;
import it.samvise85.bookshelf.persist.database.support.LabelRepository;
import it.samvise85.bookshelf.persist.repository.DatabasePersistenceUnit;
import it.samvise85.bookshelf.utils.MessagesUtil;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class LabelManagerImpl extends DatabasePersistenceUnit<Label> implements LabelManager {
	
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
	public List<Label> getList(PersistOptions options) {
		String language = null;
		if(options.getSelection() != null) {
			for(SelectionClause sel : options.getSelection()) {
				if(sel.getField().equals("lang")) {
					language = (String)sel.getValue();
				}
			}
		}
		if(language != null)
			return findByLanguage(options, language);
		return findAll(options);
	}

	private List<Label> findByLanguage(PersistOptions options, String language) {
		Pageable pageable = createPageable(options.getPagination());
		if(pageable != null)
			return convertToList(repository.findByLangOrderByLabelKeyAsc(language, pageable), options.getProjection());
		return convertToList(repository.findByLangOrderByLabelKeyAsc(language), options.getProjection());
	}

	private List<Label> findAll(PersistOptions options) {
		Pageable pageable = createPageable(options);
		if(pageable != null)
			return convertToList(repository.findAll(pageable), options.getProjection());
		Sort sort = createSort(options.getOrder());
		if(sort != null)
			return convertToList(repository.findAll(sort), options.getProjection());
		return convertToList(repository.findAll(), options.getProjection());
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

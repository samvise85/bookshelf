package it.samvise85.bookshelf.persist.database.support;

import it.samvise85.bookshelf.model.locale.Label;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LabelRepository extends PagingAndSortingRepository<Label, String> {

	Label findOneByLabelKeyAndLang(String key, String language);

	Iterable<Label> findByLangOrderByLabelKeyAsc(String language);
	Page<Label> findByLangOrderByLabelKeyAsc(String language, Pageable pageable);

}
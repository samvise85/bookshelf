package it.samvise85.bookshelf.persist.repository;

import it.samvise85.bookshelf.model.Label;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LabelRepository extends SearchRepository<Label, String> {

	Label findOneByLabelKeyAndLang(String key, String language);

	Iterable<Label> findByLangOrderByLabelKeyAsc(String language);
	Page<Label> findByLangOrderByLabelKeyAsc(String language, Pageable pageable);

}
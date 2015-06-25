package it.samvise85.bookshelf.persist.inmemory.support;

import it.samvise85.bookshelf.model.locale.Label;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LabelRepository extends PagingAndSortingRepository<Label, String> {

	Label findOneByKeyAndLang(String key, String language);

	Iterable<Label> findByLangOrderByKeyAsc(String language);
	Page<Label> findByLangOrderByKeyAsc(String language, Pageable pageable);

}
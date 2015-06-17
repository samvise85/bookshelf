package it.samvise85.bookshelf.persist.inmemory.support;

import it.samvise85.bookshelf.model.locale.Label;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface LabelRepository extends PagingAndSortingRepository<Label, String> {

	Label findOneByKeyAndLang(String key, String language);
 
}
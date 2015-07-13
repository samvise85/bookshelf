package it.samvise85.bookshelf.persist.repository;

import it.samvise85.bookshelf.persist.PersistOptions;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface SearchRepository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {
	
	List<T> search(PersistOptions options);
}

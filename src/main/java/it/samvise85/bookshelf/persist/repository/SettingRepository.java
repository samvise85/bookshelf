package it.samvise85.bookshelf.persist.repository;

import it.samvise85.bookshelf.model.Setting;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface SettingRepository extends PagingAndSortingRepository<Setting, String>{

}

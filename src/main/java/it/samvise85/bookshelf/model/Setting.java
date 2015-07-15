package it.samvise85.bookshelf.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import it.samvise85.bookshelf.model.commons.StringIdentifiable;

@Entity
public class Setting implements StringIdentifiable {

	@Id
	private String id;
	@Column
	private String value;
	
	public Setting() {}
	public Setting(String id, String value) {
		this.id = id;
		this.value = value;
	}
	@Override
	public String getId() {
		return id;
	}
	@Override
	public void setId(String id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

}

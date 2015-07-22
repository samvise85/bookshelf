package it.samvise85.bookshelf.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import it.samvise85.bookshelf.model.commons.StringIdentifiable;

@Entity
public class UserSetting implements StringIdentifiable {

	@Id
	private String id;
	@Column
	private String value;
	@Column
	private String user;
	
	public UserSetting() {}
	
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
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}

}

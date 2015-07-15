package it.samvise85.bookshelf.model;

import it.samvise85.bookshelf.model.commons.GenericIdentifiable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class UserProfile implements GenericIdentifiable<Long> {
	
	@Id
	@GeneratedValue
	private Long id;
	private String user;
	private String profile;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

}

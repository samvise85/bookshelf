package it.samvise85.bookshelf.model;

import it.samvise85.bookshelf.model.commons.GenericIdentifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Route implements GenericIdentifiable<Long> {
	
	@Id
	@GeneratedValue
	private Long id;
	@Column
	private String source;
	@Column
	private String target;
	@Column
	private String username;
	@Column
	private String status;

	@Override
	public Long getId() {
		return id;
	}
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

}

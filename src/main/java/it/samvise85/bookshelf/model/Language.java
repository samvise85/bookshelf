
package it.samvise85.bookshelf.model;

import it.samvise85.bookshelf.model.commons.GenericIdentifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Language implements GenericIdentifiable<String> {

	@Id
	private String id;
	@Column
	private Long version;
	@Column
	private Boolean def;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long update) {
		this.version = update;
	}
	public Boolean getDef() {
		return def;
	}
	public void setDef(Boolean def) {
		this.def = def;
	}
	
}

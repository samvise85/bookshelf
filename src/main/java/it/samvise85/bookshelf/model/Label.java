
package it.samvise85.bookshelf.model;

import it.samvise85.bookshelf.model.commons.GenericIdentifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Label implements GenericIdentifiable<String> {

	@Id
	private String id;
	@Column
	private String labelKey;
	@Column
	private String lang;
	@Column
	private String label;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLabelKey() {
		return labelKey;
	}
	public void setLabelKey(String labelKey) {
		this.labelKey = labelKey;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
}

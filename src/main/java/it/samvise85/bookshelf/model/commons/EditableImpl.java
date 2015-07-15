package it.samvise85.bookshelf.model.commons;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class EditableImpl implements Editable {
	
	//dates
	@Column
	protected Date creation;
	@Column
	protected Date lastModification;
	
	public Date getCreation() {
		return creation;
	}
	public void setCreation(Date creation) {
		this.creation = creation;
	}
	public Date getLastModification() {
		return lastModification;
	}
	public void setLastModification(Date lastModification) {
		this.lastModification = lastModification;
	}
}

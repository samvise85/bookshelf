package it.samvise85.bookshelf.model;

import java.util.Date;

public abstract class EditableImpl implements Editable {
	
	//dates
	private Date creation;
	private Date lastModification;
	
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

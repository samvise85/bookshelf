package it.samvise85.bookshelf.model.commons;

import java.util.Date;

public interface Editable {

	public abstract Date getCreation();

	public abstract void setCreation(Date creation);

	public abstract Date getLastModification();

	public abstract void setLastModification(Date lastModification);

}
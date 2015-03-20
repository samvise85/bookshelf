package it.samvise85.bookshelf.model;

import it.samvise85.bookshelf.persist.clauses.ExclusionClause;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;
import it.samvise85.bookshelf.persist.clauses.SimpleProjectionClause;

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
	
	protected static <T> T returnNullOrValue(ProjectionClause projection, String fieldName, T fieldValue) {
		if(projection != null && 
				((projection instanceof ExclusionClause && projection.contains(fieldName)) ||
				((projection instanceof SimpleProjectionClause) && !projection.contains(fieldName)))) return null;
		return fieldValue;
	}
}

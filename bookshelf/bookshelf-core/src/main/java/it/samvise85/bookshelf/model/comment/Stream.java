package it.samvise85.bookshelf.model.comment;

import it.samvise85.bookshelf.model.Identifiable;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@Entity
public class Stream implements Identifiable {
	//internals
	@Id
	private String id;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	@Transient
	@JsonIgnore
	@Override
	public ProjectionClause getProjection() {
		return null;
	}
	@Override
	public Stream setProjection(ProjectionClause projection) {
		return this;
	}
	
}

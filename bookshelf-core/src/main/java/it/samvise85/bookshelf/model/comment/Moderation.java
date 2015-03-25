package it.samvise85.bookshelf.model.comment;

import it.samvise85.bookshelf.model.Identifiable;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@Entity
public class Moderation implements Identifiable {
	//internals
	@Id
	private String id;
	@Column
	private String comment;
	@Column
	private String moderator;
	
	@Transient
	@JsonIgnore
	private ProjectionClause projection;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getModerator() {
		return moderator;
	}
	public void setModerator(String moderator) {
		this.moderator = moderator;
	}

	@Override
	public ProjectionClause getProjection() {
		return projection;
	}
	@Override
	public Moderation setProjection(ProjectionClause projection) {
		this.projection = projection;
		return this;
	}
}

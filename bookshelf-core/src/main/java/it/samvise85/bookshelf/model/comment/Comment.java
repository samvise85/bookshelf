package it.samvise85.bookshelf.model.comment;

import it.samvise85.bookshelf.model.Commentable;
import it.samvise85.bookshelf.model.CommentableImpl;
import it.samvise85.bookshelf.model.Editable;
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
public class Comment extends CommentableImpl implements Commentable, Editable, Identifiable {
	//internal
	@Id
	private String id;
	@Column
	private String comment;
	@Column
	private String parentStream;
	@Column
	private String user;
	@Column
	private String moderation;
	
	@Transient
	@JsonIgnore
	private ProjectionClause projection;
	
	public Comment() {};
	public Comment(String comment, String parentStream, String user) {
		super();
		this.comment = comment;
		this.parentStream = parentStream;
		this.user = user;
	}
	
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
	public String getParentStream() {
		return parentStream;
	}
	public void setParentStream(String parentStream) {
		this.parentStream = parentStream;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getModeration() {
		return moderation;
	}
	public void setModeration(String moderation) {
		this.moderation = moderation;
	}

	@Override
	public ProjectionClause getProjection() {
		return projection;
	}
	@Override
	public Comment setProjection(ProjectionClause projection) {
		this.projection = projection;
		return this;
	}
	
}

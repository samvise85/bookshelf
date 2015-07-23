package it.samvise85.bookshelf.model;

import it.samvise85.bookshelf.model.commons.CommentableImpl;
import it.samvise85.bookshelf.model.commons.StringIdentifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@Entity
public class Comment extends CommentableImpl implements StringIdentifiable {
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

}

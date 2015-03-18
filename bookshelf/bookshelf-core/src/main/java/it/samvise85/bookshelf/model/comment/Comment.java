package it.samvise85.bookshelf.model.comment;

import it.samvise85.bookshelf.model.Commentable;
import it.samvise85.bookshelf.model.CommentableImpl;
import it.samvise85.bookshelf.model.Editable;
import it.samvise85.bookshelf.model.Identifiable;
import it.samvise85.bookshelf.model.user.User;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Comment extends CommentableImpl implements Commentable, Editable, Identifiable {
	//internal
	private String id;
	private String comment;
	
	//externals
	private Stream parentStream;
	private User user;
	private Moderation moderation;
	
	public Comment() {};
	public Comment(String comment, Stream parentStream, User user) {
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
	public Stream getParentStream() {
		return parentStream;
	}
	public void setParentStream(Stream parentStream) {
		this.parentStream = parentStream;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Moderation getModeration() {
		return moderation;
	}
	public void setModeration(Moderation moderation) {
		this.moderation = moderation;
	}
	
}

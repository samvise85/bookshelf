package it.samvise85.bookshelf.model.comment;

import it.samvise85.bookshelf.model.Commentable;
import it.samvise85.bookshelf.model.Identifiable;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Stream implements Identifiable {
	//internals
	private String id;
	
	//externals
	private Commentable parent;
	private List<Comment> comments;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Commentable getParent() {
		return parent;
	}
	public void setParent(Commentable parent) {
		this.parent = parent;
	}
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	
}

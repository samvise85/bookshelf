package it.samvise85.bookshelf.model.comment;

import it.samvise85.bookshelf.model.Identifiable;
import it.samvise85.bookshelf.model.user.User;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Moderation implements Identifiable {
	//internals
	private String id;
	private String comment;
	
	//externals
	private User moderator;

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
	public User getModerator() {
		return moderator;
	}
	public void setModerator(User moderator) {
		this.moderator = moderator;
	}
}

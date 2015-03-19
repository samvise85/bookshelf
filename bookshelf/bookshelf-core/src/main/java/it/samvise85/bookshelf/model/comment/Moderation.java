package it.samvise85.bookshelf.model.comment;

import it.samvise85.bookshelf.model.Identifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@Entity
public class Moderation implements Identifiable {
	//internals
	@Id
	@GeneratedValue
	private String id;
	@Column
	private String comment;
	@Column
	private String moderator;
	
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
}

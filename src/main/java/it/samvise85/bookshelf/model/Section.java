package it.samvise85.bookshelf.model;

import it.samvise85.bookshelf.model.commons.Commentable;
import it.samvise85.bookshelf.model.commons.CommentableImpl;
import it.samvise85.bookshelf.model.commons.Editable;
import it.samvise85.bookshelf.model.commons.StringIdentifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@Entity
public class Section extends CommentableImpl implements Commentable, Editable, StringIdentifiable {
	//internal attributes
	@Id
	private String id;
	@Column
	private Integer position;
	@Column
	private String title;
	@Column
	private String synopsis;
	@Column
	private String book;

	public Section() {};
	public Section(String title, String book) {
		super();
		this.title = title;
		this.book = book;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSynopsis() {
		return synopsis;
	}
	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}
	public String getBook() {
		return book;
	}
	public void setBook(String book) {
		this.book = book;
	}
}

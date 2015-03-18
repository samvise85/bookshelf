package it.samvise85.bookshelf.model.book;

import it.samvise85.bookshelf.model.Commentable;
import it.samvise85.bookshelf.model.CommentableImpl;
import it.samvise85.bookshelf.model.Editable;
import it.samvise85.bookshelf.model.Identifiable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Chapter extends CommentableImpl implements Commentable, Editable, Identifiable {
	//internal attributes
	private String id;
	private String title;
	private String number;
	private Integer position;
	private String synopsis;
	private String text;

	//external attributes
	private String book;
	private String section;
	
	public Chapter() {};
	public Chapter(String title, String number, String text, String book) {
		super();
		this.title = title;
		this.number = number;
		this.text = text;
		this.book = book;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public Integer getPosition() {
		return position;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	public String getSynopsis() {
		return synopsis;
	}
	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getBook() {
		return book;
	}
	public void setBook(String book) {
		this.book = book;
	}
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}
	
	
}

package it.samvise85.bookshelf.model.book;

import it.samvise85.bookshelf.model.Commentable;
import it.samvise85.bookshelf.model.CommentableImpl;
import it.samvise85.bookshelf.model.Editable;
import it.samvise85.bookshelf.model.Identifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@Entity
public class Book extends CommentableImpl implements Commentable, Editable, Identifiable {
	//internal attributes

	@Column
	@GeneratedValue
	private Long generatedId;
	@Id
	private String id;
	@Column
	private String title;
	@Column
	private Integer year;
	@Column
	private String synopsis;
	@Column
	private String genre;
	@Column
	private String author;
	
	public Book() {}
	public Book(String name, String author) {
		super();
		this.title = name;
		this.author = author;
	}
	
	public Long getGeneratedId() {
		return generatedId;
	}
	public void setGeneratedId(Long generatedId) {
		this.generatedId = generatedId;
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
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public String getSynopsis() {
		return synopsis;
	}
	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	
}

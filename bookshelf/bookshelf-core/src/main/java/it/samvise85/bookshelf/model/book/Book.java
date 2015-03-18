package it.samvise85.bookshelf.model.book;

import it.samvise85.bookshelf.model.Commentable;
import it.samvise85.bookshelf.model.CommentableImpl;
import it.samvise85.bookshelf.model.Editable;
import it.samvise85.bookshelf.model.Identifiable;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Book extends CommentableImpl implements Commentable, Editable, Identifiable {
	//internal attributes
	private String id;
	private String title;
	private Integer year;
	private String synopsis;
	private String genre;
	
	//external attributes
	private String author;
	private List<String> chapters;
	private List<String> sections;
	
	public Book() {}
	public Book(String name, String author) {
		super();
		this.title = name;
		this.author = author;
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
	public List<String> getChapters() {
		return chapters;
	}
	public void setChapters(List<String> chapters) {
		this.chapters = chapters;
	}
	public List<String> getSections() {
		return sections;
	}
	public void setSections(List<String> sections) {
		this.sections = sections;
	}
	
}

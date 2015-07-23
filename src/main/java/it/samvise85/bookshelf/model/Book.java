package it.samvise85.bookshelf.model;

import it.samvise85.bookshelf.model.commons.CommentableImpl;
import it.samvise85.bookshelf.model.commons.Publishable;
import it.samvise85.bookshelf.model.commons.StringIdentifiable;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@Entity
public class Book extends CommentableImpl implements StringIdentifiable, Publishable {

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
	@Column
	private String publishingStatus;
	@Column
	private Date publishingDate; 
	
	@Transient
	private String authorname;
	@Transient
	private List<UserProfile> profiles;
	
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
//	@Override
//	public Date getCreation() {
//		return super.getCreation();
//	}
//	@Override
//	public Date getLastModification() {
//		return super.getLastModification();
//	}

	public String getAuthorname() {
		if(authorname != null) return authorname;
		else return author;
	}
	public void setAuthorname(String authorname) {
		this.authorname = authorname;
	}
	
	@Override
	public String getPublishingStatus() {
		return publishingStatus;
	}
	@Override
	public void setPublishingStatus(String status) {
		this.publishingStatus = status;
	}
	@Override
	public Date getPublishingDate() {
		return publishingDate;
	}
	@Override
	public void setPublishingDate(Date publishingDate) {
		this.publishingDate = publishingDate;
	}
}

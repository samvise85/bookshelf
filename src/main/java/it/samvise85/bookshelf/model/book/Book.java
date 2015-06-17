package it.samvise85.bookshelf.model.book;

import java.util.Date;

import it.samvise85.bookshelf.model.Commentable;
import it.samvise85.bookshelf.model.CommentableImpl;
import it.samvise85.bookshelf.model.Editable;
import it.samvise85.bookshelf.model.Projectable;
import it.samvise85.bookshelf.model.StringIdentifiable;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;
import it.samvise85.bookshelf.utils.ProjectableUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@Entity
public class Book extends CommentableImpl implements Commentable, Editable, StringIdentifiable, Projectable {
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
	
	@Transient
	@JsonIgnore
	private ProjectionClause projection;
	
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
		return returnNullOrValue("title", title);
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getYear() {
		return returnNullOrValue("year", year);
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public String getSynopsis() {
		return returnNullOrValue("synopsis", synopsis);
	}
	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}
	public String getGenre() {
		return returnNullOrValue("genre", genre);
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
	public String getAuthor() {
		return returnNullOrValue("author", author);
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	@Override
	public Date getCreation() {
		return returnNullOrValue("creation", super.getCreation());
	}
	@Override
	public Date getLastModification() {
		return returnNullOrValue("lastModification", super.getLastModification());
	}

	@Override
	public ProjectionClause getProjection() {
		return projection;
	}
	@Override
	public Book setProjection(ProjectionClause projection) {
		this.projection = projection;
		return this;
	}

	private <T> T returnNullOrValue(String fieldName, T fieldValue) {
		return ProjectableUtils.returnNullOrValue(projection, fieldName, fieldValue);
	}
}

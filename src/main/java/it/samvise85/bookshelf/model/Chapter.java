package it.samvise85.bookshelf.model;

import it.samvise85.bookshelf.model.commons.Commentable;
import it.samvise85.bookshelf.model.commons.CommentableImpl;
import it.samvise85.bookshelf.model.commons.Editable;
import it.samvise85.bookshelf.model.commons.Projectable;
import it.samvise85.bookshelf.model.commons.StringIdentifiable;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@Entity
public class Chapter extends CommentableImpl implements Commentable, Editable, StringIdentifiable, Projectable {
	//internal attributes
	@Id
	private String id;
	@Column
	private String title;
	@Column
	private String number;
	@Column
	private Integer position;
	@Column
	private String synopsis;
	@Column(length=100000)
	private String text;
	@Column
	private String book;
	@Column
	private String section;
	
	@Transient
	@JsonIgnore
	private ProjectionClause projection;

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
		return returnNullOrValue("title", title);
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getNumber() {
		return returnNullOrValue("number", number);
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public Integer getPosition() {
		return returnNullOrValue("position", position);
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	public String getSynopsis() {
		return returnNullOrValue("synopsis", synopsis);
	}
	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}
	public String getText() {
		return returnNullOrValue("text", text);
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getBook() {
		return returnNullOrValue("book", book);
	}
	public void setBook(String book) {
		this.book = book;
	}
	public String getSection() {
		return returnNullOrValue("section", section);
	}
	public void setSection(String section) {
		this.section = section;
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
	public String getStream() {
		return returnNullOrValue("stream", super.getStream());
	}
	
	@Override
	public ProjectionClause getProjection() {
		return projection;
	}
	@Override
	public Chapter setProjection(ProjectionClause projection) {
		this.projection = projection;
		return this;
	}
	
	private <T> T returnNullOrValue(String fieldName, T fieldValue) {
		return ProjectionClause.returnNullOrValue(projection, fieldName, fieldValue);
	}
	
}

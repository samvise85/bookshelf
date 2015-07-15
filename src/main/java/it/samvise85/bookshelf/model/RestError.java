package it.samvise85.bookshelf.model;

import it.samvise85.bookshelf.model.commons.GenericIdentifiable;
import it.samvise85.bookshelf.model.commons.Projectable;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class RestError implements GenericIdentifiable<Long>, Projectable {

	public static final int STACK_TRACE_MAX_LENGHT = 10000;
	
	@Id
	@GeneratedValue
	private Long id;
	@Column
	private Long restRequest;
	@Column
	private String message;
	@Lob
	@Column(name="STACK_TRACE")
	private String stackTrace;

	@Transient
	@JsonIgnore
	private ProjectionClause projection;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getRestRequest() {
		return restRequest;
	}
	public void setRestRequest(Long restRequest) {
		this.restRequest = restRequest;
	}
	public String getMessage() {
		return returnNullOrValue("message", message);
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStackTrace() {
		return returnNullOrValue("stackTrace", stackTrace);
	}
	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}
	
	@Override
	public ProjectionClause getProjection() {
		return projection;
	}
	@Override
	public RestError setProjection(ProjectionClause projection) {
		this.projection = projection;
		return this;
	}

	private <T> T returnNullOrValue(String fieldName, T fieldValue) {
		return ProjectionClause.returnNullOrValue(projection, fieldName, fieldValue);
	}
}

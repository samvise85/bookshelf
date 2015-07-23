package it.samvise85.bookshelf.model;

import it.samvise85.bookshelf.model.commons.GenericIdentifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class RestError implements GenericIdentifiable<Long> {

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
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStackTrace() {
		return stackTrace;
	}
	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}
}

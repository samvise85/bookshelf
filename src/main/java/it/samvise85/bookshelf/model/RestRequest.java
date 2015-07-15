package it.samvise85.bookshelf.model;

import java.util.Date;

import it.samvise85.bookshelf.model.commons.GenericIdentifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class RestRequest implements GenericIdentifiable<Long> {

	@Id
	@GeneratedValue
	private Long id;
	@Column
	private String requestURL;
	@Column
	private String httpMethod;
	@Column
	private String method;
	@Column
	private Date date;
	@Column
	private String username;
	@Column
	private String locale;
	@Column
	private String ip;
	@Column
	private String objectClass;
	@Lob
	@Column(name="OBJECT_JSON")
	private String objectJson;
	
	@Override
	public Long getId() {
		return id;
	}
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	public String getRequestURL() {
		return requestURL;
	}
	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getObjectClass() {
		return objectClass;
	}
	public void setObjectClass(String objectClass) {
		this.objectClass = objectClass;
	}
	public String getObjectJson() {
		return objectJson;
	}
	public void setObjectJson(String objectJson) {
		this.objectJson = objectJson;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	public String getHttpMethod() {
		return httpMethod;
	}
	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	
}

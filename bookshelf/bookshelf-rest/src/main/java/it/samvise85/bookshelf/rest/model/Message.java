package it.samvise85.bookshelf.rest.model;

public class Message {
	
	private Object object;
	private String error;
	
	public Object getObject() {
		return object;
	}
	public void setObject(Object object) {
		this.object = object;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
}

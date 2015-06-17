package it.samvise85.bookshelf.model;


public interface GenericIdentifiable<T> {
	
	public T getId();
	public void setId(T id);
}

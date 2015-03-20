package it.samvise85.bookshelf.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class CommentableImpl extends EditableImpl implements Commentable {
	
	@Column
	protected String stream;

	public String getStream() {
		return stream;
	}

	public void setStream(String stream) {
		this.stream = stream;
	}
	
}

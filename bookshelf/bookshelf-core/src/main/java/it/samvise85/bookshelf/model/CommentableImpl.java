package it.samvise85.bookshelf.model;


public abstract class CommentableImpl extends EditableImpl implements Commentable {
	
	private String stream;

	public String getStream() {
		return stream;
	}

	public void setStream(String stream) {
		this.stream = stream;
	}
	
}

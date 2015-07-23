package it.samvise85.bookshelf.model.commons;

import java.util.Date;

public interface Publishable {

	public String getPublishingStatus();
	public void setPublishingStatus(String status);
	
	public Date getPublishingDate();
	public void setPublishingDate(Date publishingDate);
	
}

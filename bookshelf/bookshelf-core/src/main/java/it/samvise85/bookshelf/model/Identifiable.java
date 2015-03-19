package it.samvise85.bookshelf.model;

import it.samvise85.bookshelf.persist.clauses.ProjectionClause;

public interface Identifiable {
	
	public String getId();
	public void setId(String id);

	public ProjectionClause getProjection();
	public Identifiable setProjection(ProjectionClause projection);
}

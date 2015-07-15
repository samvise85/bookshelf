package it.samvise85.bookshelf.model.commons;

import it.samvise85.bookshelf.persist.clauses.ProjectionClause;

public interface Projectable {

	public ProjectionClause getProjection();
	public Projectable setProjection(ProjectionClause projection);
	
}

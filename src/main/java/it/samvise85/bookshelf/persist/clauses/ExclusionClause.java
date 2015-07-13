package it.samvise85.bookshelf.persist.clauses;

public class ExclusionClause extends ProjectionClause {

	public ExclusionClause(String... fields) {
		super(ProjectionType.EXCLUSION, fields);
	}
	
}

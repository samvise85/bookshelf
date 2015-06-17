package it.samvise85.bookshelf.utils;

import it.samvise85.bookshelf.persist.clauses.ExclusionClause;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;
import it.samvise85.bookshelf.persist.clauses.SimpleProjectionClause;

public class ProjectableUtils {

	public static <T> T returnNullOrValue(ProjectionClause projection, String fieldName, T fieldValue) {
		if(projection != null && 
				((projection instanceof ExclusionClause && projection.contains(fieldName)) ||
				((projection instanceof SimpleProjectionClause) && !projection.contains(fieldName)))) return null;
		return fieldValue;
	}
}

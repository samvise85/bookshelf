package it.samvise85.bookshelf.utils;

import it.samvise85.bookshelf.persist.clauses.ExclusionClause;
import it.samvise85.bookshelf.persist.clauses.NoProjectionClause;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;
import it.samvise85.bookshelf.persist.clauses.SimpleProjectionClause;

public class UserUtils {
	public static final String CONCEALED = "";
	public static final ProjectionClause TOTAL_PROTECTION = new SimpleProjectionClause("id", "username", "admin");
	public static final ProjectionClause PASSWORD_PROTECTION = new ExclusionClause("password", "resetCode", "activationCode");
	public static final ProjectionClause NO_PROTECTION = NoProjectionClause.NO_PROJECTION;
	public static final ProjectionClause AUTHENTICATION_PROTECTION = NO_PROTECTION;
	
	public static ProjectionClause getFilter(boolean concealInfo) {
		if(concealInfo) {
			//TODO metti solo informazioni che l'utente sceglie di condividere
			return TOTAL_PROTECTION;
		}
		return PASSWORD_PROTECTION;
	}
}

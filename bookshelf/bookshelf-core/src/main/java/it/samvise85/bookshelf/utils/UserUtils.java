package it.samvise85.bookshelf.utils;

import it.samvise85.bookshelf.model.user.User;
import it.samvise85.bookshelf.persist.clauses.ExclusionClause;
import it.samvise85.bookshelf.persist.clauses.NoProjectionClause;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.springframework.security.crypto.codec.Hex;

public class UserUtils {
	public static final String CONCEALED = "";
	public static final ProjectionClause TOTAL_PROTECTION = new ProjectionClause("id", "username");
	public static final ProjectionClause PASSWORD_PROTECTION = new ExclusionClause("password", "resetCode", "activationCode");
	public static final ProjectionClause NO_PROTECTION = NoProjectionClause.NO_PROJECTION;
	public static final ProjectionClause AUTHENTICATION_PROTECTION = NO_PROTECTION;
	
	public static void resetPassword(User user) {
		user.setPassword(null);
		user.setResetCode(generateResetCode(user));
	}

	private static String generateResetCode(User user) {
		String data = user.getUsername() + ":reset:" + new Date().toString();
		MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No SHA-1 algorithm available!");
        }

        return new String(Hex.encode(digest.digest(data.getBytes())));
	}

	public static ProjectionClause getFilter(boolean concealInfo) {
		if(concealInfo) {
			//TODO metti solo informazioni che l'utente sceglie di condividere
			return TOTAL_PROTECTION;
		}
		return PASSWORD_PROTECTION;
	}
}

package it.samvise85.bookshelf.rest.security.token;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.util.StringUtils;

public class TokenManagerImpl extends TokenBasedRememberMeServices implements TokenManager {
	private static final Logger log = Logger.getLogger(TokenManagerImpl.class);

	@Deprecated
	public TokenManagerImpl() {};
	
    public TokenManagerImpl(String key, UserDetailsService userDetailsService) {
        super(key, userDetailsService);
    }
    
	@Override
	public String getToken(UserDetails userDetails) {
		return getToken(userDetails, -1L);
	}

	@Override
	public String getToken(UserDetails userDetails, Long expiration) {
		if(userDetails == null) return null;
		
		String username = userDetails.getUsername();
        String password = userDetails.getPassword();

        // If unable to find a username and password, just abort as TokenBasedRememberMeServices is
        // unable to construct a valid token in this case.
        if (!StringUtils.hasLength(username)) {
            log.debug("Unable to retrieve username");
            return "";
        }

        if (!StringUtils.hasLength(password)) {
            UserDetails user = getUserDetailsService().loadUserByUsername(username);
            password = user.getPassword();

            if (!StringUtils.hasLength(password)) {
                log.debug("Unable to obtain password for user: " + username);
                return "";
            }
        }

        String token = makeTokenSignature(expiration, username, password);
		return token;
	}

	@Override
	public boolean validate(String token, UserDetails userDetails) {
		if(userDetails == null) return false;
		String calculated = getToken(userDetails);
		boolean valid = !StringUtils.isEmpty(token) && token.equals(calculated);
		if(!valid)
			valid = makeTokenSignature(-1, userDetails.getUsername(), hashPassword(userDetails.getPassword())).equals(token);
		return valid; 
	}
	
	protected String hashPassword(String password) {
		MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No SHA-1 algorithm available!");
        }

        return new String(Hex.encode(digest.digest(password.getBytes())));
	}

    /**
     * Calculates the digital signature to be put in the cookie. Default value is
     * SHA-1 ("username:tokenExpiryTime:password:key")
     */
    protected String makeTokenSignature(long tokenExpiryTime, String username, String password) {
        String data = username + ":" + tokenExpiryTime + ":" + password + ":" + getKey();
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No SHA-1 algorithm available!");
        }

        return new String(Hex.encode(digest.digest(data.getBytes())));
    }

}

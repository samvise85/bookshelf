package it.samvise85.bookshelf.web.token;

import it.samvise85.bookshelf.utils.SHA1Digester;

import org.apache.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
			valid = makeTokenSignature(-1, userDetails.getUsername(), SHA1Digester.digest(userDetails.getPassword())).equals(token);
		return valid; 
	}
	
    /**
     * Calculates the digital signature to be put in the cookie. Default value is
     * SHA-1 ("username:tokenExpiryTime:password:key")
     */
    protected String makeTokenSignature(long tokenExpiryTime, String username, String password) {
        String data = username + ":" + tokenExpiryTime + ":" + password + ":" + getKey();
        return SHA1Digester.digest(data);
    }

}

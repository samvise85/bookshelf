package it.samvise85.bookshelf.web.token;

import org.springframework.security.core.userdetails.UserDetails;

public interface TokenManager {
    String getToken(UserDetails userDetails);
    String getToken(UserDetails userDetails, Long expiration);
	boolean validate(String token, UserDetails userDetails);
}

package it.samvise85.bookshelf.web.token;

import it.samvise85.bookshelf.web.config.SpringSecurityConfig;
import it.samvise85.bookshelf.web.security.BookshelfUserDetailsService;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
public class BookshelfTokenFilter extends GenericFilterBean {

	@Autowired
	private TokenManager tokenManager;
	@Autowired
	private BookshelfUserDetailsService userDetailsService;

	private AuthenticationManager authManager;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = ((HttpServletRequest)request);
        if(StringUtils.isNotEmpty(req.getHeader(SpringSecurityConfig.USERNAME_PARAM_NAME)) && StringUtils.isNotEmpty(req.getHeader(SpringSecurityConfig.TOKEN_PARAM_NAME))) {
        	String username = req.getHeader(SpringSecurityConfig.USERNAME_PARAM_NAME);
            String token = req.getHeader(SpringSecurityConfig.TOKEN_PARAM_NAME);
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            // validate the token
            if (tokenManager.validate(token, userDetails)) {
                // build an Authentication object with the user's info
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails((HttpServletRequest) request));
                // set the authentication into the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authManager.authenticate(authentication));         
            } else {
            	((HttpServletResponse)response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized!!!");
            }
        }
        // continue thru the filter chain
        chain.doFilter(request, response);
    }

	public void setAuthManager(AuthenticationManager authManager) {
		this.authManager = authManager;
	}
    
}
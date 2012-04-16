package com.vaadin.addon.springbase.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class SpringBaseAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	protected SpringBaseAuthenticationFilter() {
		super("/");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response)
			throws AuthenticationException, IOException,
			ServletException {
		throw new UnsupportedOperationException();
	}

	protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
		return false;
	}

	protected void performLogin(HttpServletRequest request, HttpServletResponse response,
			String username, String password, boolean remember) {

	        if (username == null) username = "";
	        if (password == null) password = "";

	        username = username.trim();

	        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

	        // Allow subclasses to set the "details" property
	        setDetails(request, authRequest);
	        //authRequest.setDetails(new WebAuthenticationDetails(request));

	        Authentication authResult;
	        try {
		        try {
		        	authResult = getAuthenticationManager().authenticate(authRequest);
			        successfulAuthentication(request, response, null, authResult);
		        } catch (AuthenticationException failed) {
		        	unsuccessfulAuthentication(request, response, failed);
//		        	return false;
		        }
	        } catch (IOException e1) {
			e1.printStackTrace();
		} catch (ServletException e2) {
			e2.printStackTrace();
		}

//		return true;
	}

//	protected String obtainPassword() {
//		SpringBaseApplication app = SpringBaseApplication.get();
//		return app.getPassword();
//	}
//
//	protected String obtainUsername() {
//		SpringBaseApplication app = SpringBaseApplication.get();
//		return app.getUsername();
//	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		            Authentication authResult) throws IOException, ServletException {
		successfulAuthentication(request, response, chain, authResult, true);
	}

	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		            Authentication authResult, boolean remember) throws IOException, ServletException {

	        SecurityContextHolder.getContext().setAuthentication(authResult);

	        if (logger.isDebugEnabled()) {
	            logger.debug("Authentication success. Updating SecurityContextHolder to contain: " + authResult);
	        }

	        if (remember) getRememberMeServices().loginSuccess(request, response, authResult);

	        // Fire event
	        if (this.eventPublisher != null) {
	            eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
	        }

	        //getSuccessHandler().onAuthenticationSuccess(request, response, authResult);  sends a redirect
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
	        SecurityContextHolder.clearContext();

	        if (logger.isDebugEnabled()) {
	            logger.debug("Authentication request failed: " + failed.toString());
	            logger.debug("Updated SecurityContextHolder to contain null Authentication");
	            logger.debug("Delegating to authentication failure handler" + getFailureHandler());
	        }

	        getRememberMeServices().loginFail(request, response);

	        //getFailureHandler().onAuthenticationFailure(request, response, failed);  send a redirect
	}

	/**
	 * Provided so that subclasses may configure what is put into the
	 * authentication request's details property.
	 *
	 * @param request
	 *                that an authentication request is being created for
	 * @param authRequest
	 *                the authentication request object that should have its
	 *                details set
	 */
	protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
	}

}

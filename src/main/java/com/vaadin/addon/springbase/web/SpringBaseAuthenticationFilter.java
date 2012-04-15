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
			String username, String password) {

	        if (username == null) username = "";
	        if (password == null) password = "";

	        username = username.trim();

	        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

	        // Allow subclasses to set the "details" property
	        setDetails(request, authRequest);
	        //authRequest.setDetails(new WebAuthenticationDetails(request));

	        Authentication authResult;
	        try {
	        	authResult = getAuthenticationManager().authenticate(authRequest);
	        } catch (AuthenticationException failed) {
	        	unsuccessfulAuthentication(request, response, failed);
	        	return;
	        }
	        successfulAuthentication(request, response, null, authResult);

		return;
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
		            Authentication authResult) {

	        SecurityContextHolder.getContext().setAuthentication(authResult);

	        /*if (logger.isDebugEnabled()) {
	            logger.debug("Authentication success. Updating SecurityContextHolder to contain: " + authResult);
	        }

	        rememberMeServices.loginSuccess(request, response, authResult);

	        // Fire event
	        if (this.eventPublisher != null) {
	            eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
	        }

	        successHandler.onAuthenticationSuccess(request, response, authResult);*/

		// TODO: redirect to the correct application URL
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) {

	        /*if (logger.isDebugEnabled()) {
	            logger.debug("Authentication request failed: " + failed.toString());
	            logger.debug("Updated SecurityContextHolder to contain null Authentication");
	            logger.debug("Delegating to authentication failure handler" + failureHandler);
	        }

	        rememberMeServices.loginFail(request, response);

	        failureHandler.onAuthenticationFailure(request, response, failed);*/
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

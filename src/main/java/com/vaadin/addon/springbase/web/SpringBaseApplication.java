package com.vaadin.addon.springbase.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dellroad.stuff.vaadin.ContextApplication;
import org.dellroad.stuff.vaadin.SpringContextApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.Window;


public class SpringBaseApplication extends SpringContextApplication implements HttpServletRequestListener {

	private static final long serialVersionUID = 3706435451855103650L;

	private final LoginForm login = new LoginForm();

	@Autowired
	private SpringBaseAuthenticationFilter springBaseAuthenticationFilter;

	private HttpServletRequest request;

	private HttpServletResponse response;

	@Override
	protected void initSpringApplication(ConfigurableWebApplicationContext arg0) {

	        Window mainWindow = new Window("Spring Base");
	        this.setMainWindow(mainWindow);

	        login.setWidth("100%");
	        login.setHeight("300px");
	        login.addListener(new LoginForm.LoginListener() {

			private static final long serialVersionUID = 1855103650370643545L;

			public void onLogin(LoginEvent event) {
	        		springBaseAuthenticationFilter.performLogin(request, response,
	        				event.getLoginParameter("username"),
	        				event.getLoginParameter("password"));
//	        		getMainWindow().showNotification(
//	        				"New Login",
//	        				"Username: " + event.getLoginParameter("username")
//	        				+ ", password: "
//	        				+ event.getLoginParameter("password"));
	        	}

	        });
	        mainWindow.addComponent(login);


//	        mainWindow.addComponent(new Button("Sign In", new Button.ClickListener() {
//	            @Override
//	            public void buttonClick(Button.ClickEvent event) {
//	            }
//	        }));

	}

	protected void doOnRequestStart(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	protected void doOnRequestEnd(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

//	public static SpringBaseApplication get() {
//		return ContextApplication.get(SpringBaseApplication.class);
//	}

//	public String getUsername() {
//		return login.getUsernameCaption();
//	}
//
//	public String getPassword() {
//		return login.getPasswordCaption();
//	}

}

package com.vaadin.addon.springbase.web;

import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dellroad.stuff.vaadin.ContextApplication;
import org.dellroad.stuff.vaadin.SpringContextApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import com.vaadin.addon.springbase.account.Account;
import com.vaadin.addon.springbase.account.Role;
import com.vaadin.addon.springbase.account.Status;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;


public class SpringBaseApplication extends SpringContextApplication implements HttpServletRequestListener {

	private static final long serialVersionUID = 3706435451855103650L;

	private static final String COMMON_FIELD_WIDTH = "12em";

	private String windowTitle;

	private Button saveChanges;

	@Autowired
	private SpringBaseAuthenticationFilter springBaseAuthenticationFilter;

	@Override
	protected void initSpringApplication(ConfigurableWebApplicationContext arg0) {

	        Window mainWindow = new Window(windowTitle);
	        this.setMainWindow(mainWindow);

	        if (!isAuthenticated()) {
	        	setMainComponent(buildSignIn());
		} else {
			setMainComponent(buildAccountDetails());
		}


	}

	protected Account getUserDetails() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Account userDetails = null;
		if (auth == null || auth instanceof AnonymousAuthenticationToken) {
			return null;
		}
		userDetails = (Account) auth.getPrincipal();
		return userDetails;
	}

	protected boolean isAuthenticated() {
//		return SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
		UserDetails userDetails = getUserDetails();
		return userDetails == null ? false : userDetails.isEnabled();
	}

	protected void logout() {
		SecurityContextHolder.getContext().setAuthentication(null);
		HttpSession session = ContextApplication.currentRequest().getSession();
		session.invalidate();
	}

	protected Layout buildAccountDetails() {
		final Account userDetails = getUserDetails();
		if (userDetails == null) {
	        	return buildSignIn();
		}
	        BeanItem<Account> userDetailsItem = new BeanItem<Account>(userDetails);

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);

	        final Form accountForm = new Form();
	        accountForm.setCaption("Account details");
	        accountForm.setWriteThrough(false); // we want explicit 'apply'
	        accountForm.setInvalidCommitted(false); // no invalid values in datamodel

	        // FieldFactory for customizing the fields and adding validators
	        accountForm.setFormFieldFactory(new UserDetailsFieldFactory());
	        accountForm.setItemDataSource(userDetailsItem); // bind to POJO via BeanItem

	        // Determines which properties are shown, and in which order:
	        accountForm.setVisibleItemProperties(Arrays.asList(new String[] {
	                "username", "emailAddress", "createdOn", "firstName", "lastName", "userRole" }));

	        // Add form to layout
	        layout.addComponent(accountForm);

	        // The cancel / apply buttons
	        HorizontalLayout buttons = new HorizontalLayout();
	        buttons.setSpacing(true);
//	        Button discardChanges = new Button("Discard changes",
//	                new Button.ClickListener() {
//	                    public void buttonClick(ClickEvent event) {
//	                        personForm.discard();
//	                    }
//	                });
//	        discardChanges.setStyleName(BaseTheme.BUTTON_LINK);
//	        buttons.addComponent(discardChanges);
//	        buttons.setComponentAlignment(discardChanges, Alignment.MIDDLE_LEFT);

	        saveChanges = new Button("Save changes", new Button.ClickListener() {

			private static final long serialVersionUID = 1036503706435451855L;

			public void buttonClick(ClickEvent event) {
	                try {
	                    accountForm.commit();
		                userDetails.merge();
		                saveChanges.setEnabled(false);
	                } catch (Exception e) {
	                    // Ignored, we'll let the Form handle the errors
	                }
	            }
	        });
	        saveChanges.setEnabled(false);
	        buttons.addComponent(saveChanges);

	        buttons.addComponent(new Button("Change password", new Button.ClickListener() {

			private static final long serialVersionUID = 1036503706435451855L;

			public void buttonClick(ClickEvent event) {
	                try {
	                    setMainComponent(buildChangePassword());
	                } catch (Exception e) {
	                    // Ignored, we'll let the Form handle the errors
	                }
	            }
	        }));

	        Button signOut = new Button("Sign out", new Button.ClickListener() {
	            @Override
	            public void buttonClick(Button.ClickEvent event) {
	        	    accountForm.discard();
	        	    logout();
	            }
	        });
	        signOut.setStyleName(BaseTheme.BUTTON_LINK);
	        buttons.addComponent(signOut);
	        buttons.setComponentAlignment(signOut, Alignment.MIDDLE_LEFT);

	        accountForm.getFooter().addComponent(buttons);
	        accountForm.getFooter().setMargin(true, true, false, false);

		return layout;
	}

	protected Layout buildRegisterAccount() {
		final Account userDetails = new Account();
		userDetails.setStatus(Status.ACTIVE);
		userDetails.setUserRole(Role.ROLE_USER);

	        BeanItem<Account> userDetailsItem = new BeanItem<Account>(userDetails);

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);

	        final Form registrationForm = new Form();
	        registrationForm.setCaption("Registration details");
	        registrationForm.setWriteThrough(false); // we want explicit 'apply'
	        registrationForm.setInvalidCommitted(false); // no invalid values in datamodel

	        // FieldFactory for customizing the fields and adding validators
	        registrationForm.setFormFieldFactory(new RegistrationFieldFactory());
	        registrationForm.setItemDataSource(userDetailsItem); // bind to POJO via BeanItem

	        // Determines which properties are shown, and in which order:
	        registrationForm.setVisibleItemProperties(Arrays.asList(new String[] {
	                "username", "emailAddress", "password" }));


//	        layout.addComponent(new Button("Sign In", new Button.ClickListener() {
//	            @Override
//	            public void buttonClick(Button.ClickEvent event) {
//		        	setMainComponent(buildSignIn());
//	            }
//	        }));

	        // Add form to layout
	        layout.addComponent(registrationForm);

	        // The cancel / apply buttons
	        HorizontalLayout buttons = new HorizontalLayout();
	        buttons.setSpacing(true);

	        Button submit = new Button("Submit", new Button.ClickListener() {

			private static final long serialVersionUID = 1036503706435451855L;

			public void buttonClick(ClickEvent event) {
//	                try {
	                	registrationForm.commit();
//	                } catch (Exception e) {
//	                    // Ignored, we'll let the Form handle the errors
//	                }
	                userDetails.persist();
                	boolean success = springBaseAuthenticationFilter.performLogin(
        				ContextApplication.currentRequest(),
        				ContextApplication.currentResponse(),
        				userDetails.getUsername(), userDetails.getPassword());
                	assert success;
	                setMainComponent(buildAccountDetails());
	            }
	        });
	        buttons.addComponent(submit);

	        Button signOut = new Button("Cancel", new Button.ClickListener() {
	            @Override
	            public void buttonClick(Button.ClickEvent event) {
	        	    registrationForm.discard();
	        	    setMainComponent(buildSignIn());
	            }
	        });
	        signOut.setStyleName(BaseTheme.BUTTON_LINK);
	        buttons.addComponent(signOut);
	        buttons.setComponentAlignment(signOut, Alignment.MIDDLE_LEFT);

	        registrationForm.getFooter().addComponent(buttons);
	        registrationForm.getFooter().setMargin(true, true, false, false);

		return layout;
	}

	protected Layout buildChangePassword() {
		final Account userDetails = getUserDetails();
		if (userDetails == null) return buildSignIn();

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);

	        HorizontalLayout buttons = new HorizontalLayout();
	        buttons.setSpacing(true);

	        buttons.addComponent(new Button("Apply", new Button.ClickListener() {
			private static final long serialVersionUID = 3545185510706436503L;
			public void buttonClick(ClickEvent event) {
	                try {
	                    //personForm.commit();
                            setMainComponent(buildAccountDetails());
	                } catch (Exception e) {
	                    // Ignored, we'll let the Form handle the errors
	                }
	            }
	        }));

	        Button cancel = new Button("Cancel",
	                new Button.ClickListener() {
			    private static final long serialVersionUID = 4365033545185510706L;
	                    public void buttonClick(ClickEvent event) {
	                        //personForm.discard();
                                setMainComponent(buildAccountDetails());
	                    }
	                });
	        cancel.setStyleName(BaseTheme.BUTTON_LINK);
	        buttons.addComponent(cancel);
	        buttons.setComponentAlignment(cancel, Alignment.MIDDLE_LEFT);

	        layout.addComponent(buttons);
		return layout;
	}

	protected Layout buildSignIn() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);

		LoginForm login = new LoginForm();
		login.setLoginButtonCaption("Sign in");
	        login.addListener(new LoginForm.LoginListener() {

			private static final long serialVersionUID = 1855103650370643545L;

			public void onLogin(LoginEvent event) {
				String username = event.getLoginParameter("username");
				String password = event.getLoginParameter("password");

	        		boolean success = springBaseAuthenticationFilter.performLogin(
	        				ContextApplication.currentRequest(),
	        				ContextApplication.currentResponse(),
	        				username, password);

	        		if (success) {
	        			String desc  = "";
	        			Account acc = getUserDetails();
	        			if (null != acc) {
	        				if (acc.getLastSignIn() != null) {
		        				desc = "Last sign in: " + acc.getLastSignIn().toString();
		        			} else {
		        				desc = "First time signing in";
		        			}
	        				acc.setLastSignIn(new Date());
	        		        }
		        		getMainWindow().showNotification(
		        				"Signed in as: " + username,
		        				desc,
		        				Notification.TYPE_TRAY_NOTIFICATION);
		        		setMainComponent(buildAccountDetails());
	        		} else {
					getMainWindow().showNotification("Login Failed",
		        				"Incorrect username (" + username + ") or password",
		        				Notification.TYPE_TRAY_NOTIFICATION);
			        	setMainComponent(buildSignIn());
				}
	        	}

	        });
	        layout.addComponent(login);

	        Button register = new Button("Register", new Button.ClickListener() {
	            @Override
	            public void buttonClick(Button.ClickEvent event) {
	        	    setMainComponent(buildRegisterAccount());
	            }
	        });
	        register.setStyleName(BaseTheme.BUTTON_LINK);
	        layout.addComponent(register);
	        layout.setComponentAlignment(register, Alignment.MIDDLE_LEFT);

	        return layout;
	}

	protected void setMainComponent(Layout layout) {
		getMainWindow().setContent(layout);
	}

	private class AccountChangeListener implements ValueChangeListener {

		@Override
		public void valueChange(ValueChangeEvent event) {
			if (saveChanges != null) saveChanges.setEnabled(true);
		}

	}

	private class UserDetailsFieldFactory extends DefaultFieldFactory {

		private static final long serialVersionUID = 4518551037064353650L;

	        final ComboBox roles = new ComboBox("Role");

	        public UserDetailsFieldFactory() {
	            roles.setWidth(COMMON_FIELD_WIDTH);
	            roles.addItem(Role.ROLE_ADMIN);
	            roles.addItem(Role.ROLE_USER);
	            roles.setNullSelectionAllowed(false);
//	            roles.setImmediate(true);
//	            roles.addListener(new AccountChangeListener());
	            roles.setReadOnly(true);
	        }

		@Override
	        public Field createField(Item item, Object propertyId,
	                Component uiContext) {
	            Field f;
	            if ("userRole".equals(propertyId)) {
	                    return roles;
	            } else if ("password".equals(propertyId)) {
	                // Create a password field so the password is not shown
	                f = createPasswordField(propertyId);
	            } else {
	                // Use the super class to create a suitable field base on the
	                // property type.
	                f = super.createField(item, propertyId, uiContext);
	            }

	            f.addListener(new AccountChangeListener());

	            if ("firstName".equals(propertyId)) {
	                TextField tf = (TextField) f;
	                tf.setImmediate(true);
//	                tf.setRequired(true);
	                tf.setRequiredError("Please enter a First Name");
	                tf.setWidth(COMMON_FIELD_WIDTH);
	                tf.addValidator(new StringLengthValidator(
	                        "First Name must be 3-25 characters", 3, 25, false));
	            } else if ("lastName".equals(propertyId)) {
	                TextField tf = (TextField) f;
	                tf.setImmediate(true);
//	                tf.setRequired(true);
	                tf.setRequiredError("Please enter a Last Name");
	                tf.setWidth(COMMON_FIELD_WIDTH);
	                tf.addValidator(new StringLengthValidator(
	                        "Last Name must be 3-50 characters", 3, 50, false));
	            } else if ("username".equals(propertyId)) {
		        TextField tf = (TextField) f;
	                tf.setReadOnly(true);
	                tf.setWidth(COMMON_FIELD_WIDTH);
	            } else if ("emailAddress".equals(propertyId)) {
		        TextField tf = (TextField) f;
	                tf.setReadOnly(true);
	                tf.setWidth(COMMON_FIELD_WIDTH);
	            } else if ("createdOn".equals(propertyId)) {
		        DateField df = (DateField) f;
	                df.setReadOnly(true);
	                df.setWidth(COMMON_FIELD_WIDTH);
	            } else if ("lastSignIn".equals(propertyId)) {
	        	DateField df = (DateField) f;
	                df.setReadOnly(true);
	                df.setWidth(COMMON_FIELD_WIDTH);
//	            } else if ("password".equals(propertyId)) {
//	                PasswordField pf = (PasswordField) f;
//	                pf.setImmediate(true);
//	                pf.setRequired(true);
//	                pf.setRequiredError("Please enter a password");
//	                pf.setWidth("10em");
//	                pf.addValidator(new StringLengthValidator(
//	                        "Password must be 6-20 characters", 6, 20, false));
//	            } else if ("shoesize".equals(propertyId)) {
//	                TextField tf = (TextField) f;
//	                tf.setImmediate(true);
//	                tf.setNullRepresentation("");
//	                tf.setNullSettingAllowed(true);
//	                tf.addValidator(new IntegerValidator(
//	                        "Shoe size must be an Integer"));
//	                tf.setWidth("2em");
//	            } else if ("uuid".equals(propertyId)) {
//	                TextField tf = (TextField) f;
//	                tf.setWidth("20em");
	            }

	            return f;
	        }

	        private PasswordField createPasswordField(Object propertyId) {
	            PasswordField pf = new PasswordField();
	            pf.setCaption(DefaultFieldFactory
	                    .createCaptionByPropertyId(propertyId));
	            return pf;
	        }
	    }

	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
	}

	private class RegistrationFieldFactory extends DefaultFieldFactory {

		private static final long serialVersionUID = 4353650455103718506L;

		@Override
	        public Field createField(Item item, Object propertyId,
	                Component uiContext) {
	            Field f;
	            if ("password".equals(propertyId)) {
	                // Create a password field so the password is not shown
	                f = createPasswordField(propertyId);
	            } else {
	                // Use the super class to create a suitable field base on the
	                // property type.
	                f = super.createField(item, propertyId, uiContext);
	            }

	            f.addListener(new AccountChangeListener());

	            if ("username".equals(propertyId)) {
		        TextField tf = (TextField) f;
	                tf.setRequired(true);
	                tf.setRequiredError("Please enter a username");
	                tf.setWidth(COMMON_FIELD_WIDTH);
	                tf.addValidator(new StringLengthValidator(
		                        "Last Name must be 3-30 characters", 3, 30, false));
	            } else if ("emailAddress".equals(propertyId)) {
		        TextField tf = (TextField) f;
	                tf.setRequired(true);
	                tf.setRequiredError("Please enter an email address");
	                tf.setWidth(COMMON_FIELD_WIDTH);
	                tf.addValidator(new EmailValidator(
	                		"Please enter a syntactically valid email address"));
	            } else if ("password".equals(propertyId)) {
	                PasswordField pf = (PasswordField) f;
	                pf.setRequired(true);
	                pf.setRequiredError("Please enter a password");
	                pf.setWidth("10em");
	                pf.addValidator(new StringLengthValidator(
	                        "Password must be 6-20 characters", 6, 20, false));
	            }

	            return f;
	        }

	        private PasswordField createPasswordField(Object propertyId) {
	            PasswordField pf = new PasswordField();
	            pf.setCaption(DefaultFieldFactory.createCaptionByPropertyId(propertyId));
	            return pf;
	        }
	    }

}

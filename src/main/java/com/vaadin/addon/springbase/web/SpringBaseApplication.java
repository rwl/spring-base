package com.vaadin.addon.springbase.web;

import java.util.Arrays;
import java.util.Date;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpSession;

import org.dellroad.stuff.vaadin.ContextApplication;
import org.dellroad.stuff.vaadin.SpringContextApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.AbstractValidator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
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
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.ChameleonTheme;


public class SpringBaseApplication extends SpringContextApplication implements HttpServletRequestListener {

	private static final long serialVersionUID = 3706435451855103650L;

	private static final String COMMON_FIELD_WIDTH = "100%";

	private static final ThemeResource GOOGLE_ICON = new ThemeResource("img/google.png");
	private static final ThemeResource GITHUB_ICON = new ThemeResource("img/github.png");
	private static final ThemeResource FACEBOOK_ICON = new ThemeResource("img/facebook.png");
	private static final ThemeResource TWITTER_ICON = new ThemeResource("img/twitter.png");

	private String windowTitle;

	private Button saveChanges;

	@Autowired
	private SpringBaseAuthenticationFilter springBaseAuthenticationFilter;

	@Override
	protected void initSpringApplication(ConfigurableWebApplicationContext arg0) {

	        Window mainWindow = new Window(windowTitle);
	        mainWindow.setTheme("spring-account");  // select window theme
	        this.setMainWindow(mainWindow);

	        if (!isAuthenticated()) {
	        	setMainComponent(buildWelcome());
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

	protected boolean usernameTaken(String username) {
		TypedQuery<Account> query = Account.findAccountsByUsername(username);
		try {
			query.getSingleResult();
		} catch (NoResultException e1) {
			return false;
		} catch (EmptyResultDataAccessException e2) {
			return false;
		}
		return true;
	}

	protected void logout() {
		SecurityContextHolder.getContext().setAuthentication(null);
		HttpSession session = ContextApplication.currentRequest().getSession();
		session.invalidate();
	}

	protected void setMainComponent(Layout layout) {
//		layout.setSizeUndefined();

//		Panel panel = new Panel();
//		panel.setWidth("600px");
//		panel.addComponent(layout);

		VerticalLayout mainlayout = new VerticalLayout();
		mainlayout.setWidth("480px");
		mainlayout.setMargin(true);
		mainlayout.addStyleName("mainlayout");
		mainlayout.addComponent(layout);

	        VerticalLayout outer = new VerticalLayout();
	        outer.addComponent(mainlayout);
	        outer.setComponentAlignment(mainlayout, Alignment.TOP_CENTER);
		getMainWindow().setContent(outer);
	}

	protected Layout buildAccountDetails() {
		final Account userDetails = getUserDetails();
		if (userDetails == null) return buildWelcome();
	        BeanItem<Account> userDetailsItem = new BeanItem<Account>(userDetails);

		VerticalLayout layout = new VerticalLayout();
//		layout.setSpacing(true);

		HorizontalLayout header = new HorizontalLayout();
		header.setStyleName("header");
		header.setWidth("100%");
		header.setMargin(true);

		HorizontalLayout title = new HorizontalLayout();
		title.setSpacing(true);
//		Label label = new Label("<h3>" + userDetails.getUsername() + "</h3>");
		Label label = new Label("Account");
//	        label.setContentMode(Label.CONTENT_XHTML);
	        title.addComponent(label);
	        title.setComponentAlignment(label, Alignment.MIDDLE_LEFT);

	        Button signOut = new Button("(Sign out)", new Button.ClickListener() {
	            @Override
	            public void buttonClick(Button.ClickEvent event) {
	        	    //accountForm.discard();
	        	    logout();
	            }
	        });
	        signOut.setStyleName(BaseTheme.BUTTON_LINK);
	        signOut.addStyleName("header");
	        title.addComponent(signOut);
	        title.setComponentAlignment(signOut, Alignment.MIDDLE_LEFT);

	        header.addComponent(title);
	        header.setComponentAlignment(title, Alignment.MIDDLE_LEFT);

	        layout.addComponent(header);


	        final Form accountForm = new Form();
	        accountForm.setWidth("100%");
	        accountForm.setCaption(userDetails.getUsername() + "'s profile");
	        accountForm.setWriteThrough(false); // we want explicit 'apply'
	        accountForm.setInvalidCommitted(false); // no invalid values in datamodel

	        // FieldFactory for customizing the fields and adding validators
	        accountForm.setFormFieldFactory(new UserDetailsFieldFactory());
	        accountForm.setItemDataSource(userDetailsItem); // bind to POJO via BeanItem

	        // Determines which properties are shown, and in which order:
	        accountForm.setVisibleItemProperties(Arrays.asList(new String[] {
	                "emailAddress", "createdOn", "firstName", "lastName", "location" }));

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
	                } catch (Exception e) {
	                    // Ignored, we'll let the Form handle the errors
	                    return;
	                }
	                userDetails.merge();
	                saveChanges.setEnabled(false);
	            }
	        });
	        saveChanges.setEnabled(false);
	        buttons.addComponent(saveChanges);

	        Button change = new Button("Change password", new Button.ClickListener() {

			private static final long serialVersionUID = 1036503706435451855L;

			public void buttonClick(ClickEvent event) {
	                try {
	                    setMainComponent(buildChangePassword());
	                } catch (Exception e) {
	                    // Ignored, we'll let the Form handle the errors
	                }
	            }
	        });
	        change.setStyleName(BaseTheme.BUTTON_LINK);
	        buttons.addComponent(change);
	        buttons.setComponentAlignment(change, Alignment.MIDDLE_LEFT);

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

		HorizontalLayout header = new HorizontalLayout();
		header.setStyleName("header");
		header.setWidth("100%");
		header.setMargin(true);

		HorizontalLayout title = new HorizontalLayout();
		title.setSpacing(true);
		Label label = new Label("Register");
	        title.addComponent(label);
	        title.setComponentAlignment(label, Alignment.MIDDLE_LEFT);

	        Button logIn = new Button("(Log in)");
	        logIn.addStyleName(BaseTheme.BUTTON_LINK);
	        logIn.addStyleName("header");
	        title.addComponent(logIn);
	        title.setComponentAlignment(logIn, Alignment.MIDDLE_LEFT);

	        header.addComponent(title);
	        header.setComponentAlignment(title, Alignment.MIDDLE_LEFT);

	        layout.addComponent(header);

		// registration form
	        final Form registrationForm = new Form();
//	        registrationForm.setCaption("Registration details");
	        registrationForm.setWriteThrough(false); // we want explicit 'apply'
	        registrationForm.setInvalidCommitted(false); // no invalid values in datamodel

	        // FieldFactory for customizing the fields and adding validators
	        registrationForm.setFormFieldFactory(new RegistrationFieldFactory());
	        registrationForm.setItemDataSource(userDetailsItem); // bind to POJO via BeanItem

	        // Determines which properties are shown, and in which order:
	        registrationForm.setVisibleItemProperties(Arrays.asList(new String[] {
	                "username", "emailAddress", "password" }));


	        // Add form to layout
	        layout.addComponent(registrationForm);

	        final PasswordField confirm = new PasswordField("Confirm password");
	        confirm.setRequired(true);
	        confirm.setInputPrompt("Password");
	        confirm.setWidth(COMMON_FIELD_WIDTH);
                confirm.addValidator(new AbstractValidator("Passwords do not match") {

			@Override
			public boolean isValid(Object value) {
				String password = (String) value;
				PasswordField field = (PasswordField) registrationForm.getField("password");
				return password.equals((String) field.getValue());
			}
		});
                confirm.addValidator(new StringLengthValidator(
                        "Password must be 7-64 characters", 7, 64, false));
	        registrationForm.getLayout().addComponent(confirm);
//	        registrationForm.addField("password", confirm);


	        // The cancel / apply buttons
	        HorizontalLayout buttons = new HorizontalLayout();
	        buttons.setSpacing(true);

	        Button submit = new Button("Create a new account", new Button.ClickListener() {

			private static final long serialVersionUID = 1036503706435451855L;

			public void buttonClick(ClickEvent event) {
			try {
				confirm.validate();
			} catch (InvalidValueException e) {
		                // Ignored, we'll let the Form handle the errors
				return;
			}
	                try {
	                	registrationForm.commit();
	                } catch (Exception e) {
	                    // Ignored, we'll let the Form handle the errors
	                    return;
	                }
	                userDetails.persist();
                	springBaseAuthenticationFilter.performLogin(
        				ContextApplication.currentRequest(),
        				ContextApplication.currentResponse(),
        				userDetails.getUsername(), userDetails.getPassword(), false);
                	assert isAuthenticated();
	                setMainComponent(buildAccountDetails());
	            }
	        });
	        submit.addStyleName(ChameleonTheme.BUTTON_WIDE);
	        buttons.addComponent(submit);

	        logIn.addListener(new Button.ClickListener() {
	            @Override
	            public void buttonClick(Button.ClickEvent event) {
	        	    registrationForm.discard();
	        	    setMainComponent(buildWelcome());
	            }
	        });

	        registrationForm.getFooter().addComponent(buttons);
	        registrationForm.getFooter().setMargin(true, true, false, false);

		return layout;
	}

	protected Layout buildChangePassword() {
		final Account userDetails = getUserDetails();
		if (userDetails == null) return buildWelcome();

		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);

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

	protected Layout buildWelcome() {
		/*HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(true);
		layout.setSizeFull();

		Button b = new Button("Sign In");
	        b.setStyleName(BaseTheme.BUTTON_LINK);
	        b.setDescription("Sign in with your username and password");
	        b.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				setMainComponent(buildSignIn());
			}
		});
		layout.addComponent(b);
		layout.setComponentAlignment(b, Alignment.TOP_CENTER);

		return layout;*/
		return buildSignIn();
	}

	protected Layout buildSignIn() {
//		HorizontalLayout layout = new HorizontalLayout();
		VerticalLayout layout = new VerticalLayout();
		layout.addStyleName("login");
		layout.setSpacing(true);

		HorizontalLayout header = new HorizontalLayout();
		header.setStyleName("header");
		header.setWidth("100%");
		header.setMargin(true);

		HorizontalLayout title = new HorizontalLayout();
		title.setSpacing(true);
//		title.setWidth("400px");

	        Label label = new Label("Log in");
//	        label.setContentMode(Label.CONTENT_XHTML);
	        title.addComponent(label);
	        title.setComponentAlignment(label, Alignment.MIDDLE_LEFT);

	        Button register = new Button("(Register)", new Button.ClickListener() {
	            @Override
	            public void buttonClick(Button.ClickEvent event) {
	        	    setMainComponent(buildRegisterAccount());
	            }
	        });
	        register.addStyleName(BaseTheme.BUTTON_LINK);
	        register.addStyleName("header");
	        title.addComponent(register);
	        title.setComponentAlignment(register, Alignment.MIDDLE_LEFT);

	        header.addComponent(title);
	        header.setComponentAlignment(title, Alignment.MIDDLE_LEFT);

	        Button forgot = new Button("Forgot your password?", new Button.ClickListener() {
	            @Override
	            public void buttonClick(Button.ClickEvent event) {
	        	    setMainComponent(buildForgotPassword());
	            }
	        });
	        forgot.addStyleName(BaseTheme.BUTTON_LINK);
	        forgot.addStyleName("header");
	        header.addComponent(forgot);
	        header.setComponentAlignment(forgot, Alignment.MIDDLE_RIGHT);

	        layout.addComponent(header);

	        HorizontalLayout body = new HorizontalLayout();
	        body.setWidth("100%");
	        body.setSpacing(true);

	        // login form
		LoginForm login = new AccountLoginForm();

//	        login.setWidth("400px");
//	        login.setHeight("200px");
	        login.setWidth("100%");
	        login.setHeight("100%");

//		login.setStyleName("account");
		login.setLoginButtonCaption("Log in");
		login.setUsernameCaption("Login or Email");
	        login.addListener(new LoginForm.LoginListener() {

			private static final long serialVersionUID = 1855103650370643545L;

			public void onLogin(LoginEvent event) {
				String username = event.getLoginParameter("username");
				String password = event.getLoginParameter("password");
				boolean remember = event.getLoginParameter("remember") != null;

	        		springBaseAuthenticationFilter.performLogin(
	        				ContextApplication.currentRequest(),
	        				ContextApplication.currentResponse(),
	        				username, password, remember);

	        		if (isAuthenticated()) {
	        			String desc  = "";
	        			Account acc = getUserDetails();
	        			if (null != acc) {
	        				if (acc.getLastSignIn() != null) {
		        				desc = "Last sign in: " + acc.getLastSignIn().toString();
		        			} else {
		        				desc = "";
		        			}
	        				acc.setLastSignIn(new Date());
	        		        }
		        		getMainWindow().showNotification(
		        				"Signed in as: " + username,
		        				desc,
		        				Notification.TYPE_HUMANIZED_MESSAGE);
		        		setMainComponent(buildAccountDetails());
	        		} else {
					getMainWindow().showNotification("Login Failed",
		        				"Incorrect username (" + username + ") or password",
		        				Notification.TYPE_WARNING_MESSAGE);
			        	setMainComponent(buildSignIn());
				}
	        	}

	        });

	        // Login form is by default 100% width and height, so consider using it
	        // inside a sized Panel or Window.
//	        Panel panel = new Panel();
//	        panel.addComponent(login);
//	        panel.setHeight("200px");
//	        panel.setWidth("400px");
//	        layout.addComponent(panel);
	        body.addComponent(login);

	        VerticalLayout rhs = new VerticalLayout();
	        rhs.setSizeFull();

	        Label with = new Label("Sign in with");
	        rhs.addComponent(with);
	        rhs.setComponentAlignment(with, Alignment.TOP_LEFT);

	        VerticalLayout buttons = new VerticalLayout();
	        buttons.setSizeUndefined();

	        Button google = new Button("Google", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
			}
		});
	        google.setIcon(GOOGLE_ICON);
	        google.addStyleName(BaseTheme.BUTTON_LINK);
	        buttons.addComponent(google);

	        Button github = new Button("GitHub", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
			}
		});
	        github.setIcon(GITHUB_ICON);
	        github.addStyleName(BaseTheme.BUTTON_LINK);
	        buttons.addComponent(github);

	        Button twitter = new Button("Twitter", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
			}
		});
	        twitter.setIcon(TWITTER_ICON);
	        twitter.addStyleName(BaseTheme.BUTTON_LINK);
	        buttons.addComponent(twitter);

	        Button facebook = new Button("Facebook", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				// TODO Auto-generated method stub
			}
		});
	        facebook.setIcon(FACEBOOK_ICON);
	        facebook.addStyleName(BaseTheme.BUTTON_LINK);
	        buttons.addComponent(facebook);

	        rhs.addComponent(buttons);
	        rhs.setComponentAlignment(buttons, Alignment.MIDDLE_CENTER);

	        body.addComponent(rhs);

	        layout.addComponent(body);

	        return layout;
	}


	protected Layout buildForgotPassword() {
//		HorizontalLayout layout = new HorizontalLayout();
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);

	        Button cancel = new Button("Cancel",
	                new Button.ClickListener() {
			    private static final long serialVersionUID = 4365033545185510706L;
	                    public void buttonClick(ClickEvent event) {
	                        //personForm.discard();
                                setMainComponent(buildAccountDetails());
	                    }
	                });
	        cancel.setStyleName(BaseTheme.BUTTON_LINK);
	        layout.addComponent(cancel);
	        layout.setComponentAlignment(cancel, Alignment.MIDDLE_LEFT);

		return layout;
	}

	private class AccountChangeListener implements ValueChangeListener {

		@Override
		public void valueChange(ValueChangeEvent event) {
			if (saveChanges != null) saveChanges.setEnabled(true);
		}

	}

	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
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
//	                tf.setRequiredError("Please enter a First Name");
	                tf.setWidth(COMMON_FIELD_WIDTH);
	                tf.addValidator(new StringLengthValidator(
	                        "First name must be 3-32 characters", 3, 32, false));
	            } else if ("lastName".equals(propertyId)) {
	                TextField tf = (TextField) f;
	                tf.setImmediate(true);
//	                tf.setRequired(true);
//	                tf.setRequiredError("Please enter a Last Name");
	                tf.setWidth(COMMON_FIELD_WIDTH);
	                tf.addValidator(new StringLengthValidator(
	                        "Last name must be less than 64 characters", 0, 64, false));
	            } else if ("username".equals(propertyId)) {
		        TextField tf = (TextField) f;
	                tf.setReadOnly(true);
	                tf.setRequiredError("");
	                tf.setWidth(COMMON_FIELD_WIDTH);
	                tf.addValidator(new StringLengthValidator(
		                "Username must be less than 32 characters", 0, 32, false));
	            } else if ("emailAddress".equals(propertyId)) {
		        TextField tf = (TextField) f;
	                tf.setImmediate(true);
	                tf.setWidth(COMMON_FIELD_WIDTH);
	                tf.addValidator(new EmailValidator(
	                	"Please enter a syntactically valid email address"));
	                tf.setCaption("Email");
	            } else if ("location".equals(propertyId)) {
		        TextField tf = (TextField) f;
	                tf.setImmediate(true);
	                tf.addValidator(new StringLengthValidator(
		                "Location must be less than 32 characters", 0, 32, false));
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
		        tf.setInputPrompt("Username");
	                tf.setRequired(true);
	                tf.setRequiredError("Username must be alphanumeric and between 3 and 32 characters");
	                tf.setWidth(COMMON_FIELD_WIDTH);
	                tf.addValidator(new StringLengthValidator(
		                        "Last name must be 3-32 characters", 3, 32, false));

	                tf.setTextChangeEventMode(TextChangeEventMode.LAZY);
	                tf.setTextChangeTimeout(1000);
	                tf.addListener(new FieldEvents.TextChangeListener() {

	                    public void textChange(TextChangeEvent event) {
	                	String username = event.getText();
	                	if (username.length() < 3) return;
	                	boolean taken = usernameTaken(username);
	                	TextField tf = (TextField) event.getComponent();
	                	if (taken) {
	                		getMainWindow().showNotification("Username taken: " + username,
		        				Notification.TYPE_WARNING_MESSAGE);
	                	} else {
	                		getMainWindow().showNotification("Username available: " + username,
		        				Notification.TYPE_HUMANIZED_MESSAGE);
	                	}
	                    }
	                });

	                tf.addValidator(new AbstractValidator("Username unavailable") {

				@Override
				public boolean isValid(Object value) {
					String username = (String) value;
					setErrorMessage("Username \"" + username + "\" is already taken");
					return !usernameTaken(username);
				}
			});


	            } else if ("emailAddress".equals(propertyId)) {
		        TextField tf = (TextField) f;
		        tf.setInputPrompt("Email address");
	                //tf.setRequired(true);
	                tf.setRequiredError("Required for password reset");
	                tf.setWidth(COMMON_FIELD_WIDTH);
	                tf.addValidator(new EmailValidator(
	                		"Please enter a syntactically valid email address"));
	            } else if ("password".equals(propertyId)) {
	                PasswordField pf = (PasswordField) f;
		        pf.setInputPrompt("Password");
	                pf.setRequired(true);
	                pf.setRequiredError("Password must at least 7 characters");
	                pf.setWidth(COMMON_FIELD_WIDTH);
	                pf.addValidator(new StringLengthValidator(
	                        "Password must be 7-64 characters", 7, 64, false));
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

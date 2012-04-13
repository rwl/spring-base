package com.vaadin.addon.springbase.account;

import java.util.Collection;
import java.util.HashSet;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(identifierColumn = "user_id", table = "account")
public class Account implements UserDetails {

    private static final long serialVersionUID = -5706435451865101650L;

    @NotNull
    private String username;

    @NotNull
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private Role userRole;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email_address")
    private String emailAddress;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
	    return status == Status.ACTIVE;
    }

    @Override
    public Collection<org.springframework.security.core.authority.SimpleGrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> authorities = new HashSet<SimpleGrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(userRole.name()));
        return authorities;
    }

}

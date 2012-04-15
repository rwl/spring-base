// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.vaadin.addon.springbase.account;

import com.vaadin.addon.springbase.account.Account;
import com.vaadin.addon.springbase.account.Role;
import com.vaadin.addon.springbase.account.Status;
import java.util.Date;

privileged aspect Account_Roo_JavaBean {
    
    public void Account.setUsername(String username) {
        this.username = username;
    }
    
    public void Account.setPassword(String password) {
        this.password = password;
    }
    
    public Role Account.getUserRole() {
        return this.userRole;
    }
    
    public void Account.setUserRole(Role userRole) {
        this.userRole = userRole;
    }
    
    public String Account.getFirstName() {
        return this.firstName;
    }
    
    public void Account.setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String Account.getLastName() {
        return this.lastName;
    }
    
    public void Account.setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String Account.getEmailAddress() {
        return this.emailAddress;
    }
    
    public void Account.setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    public Status Account.getStatus() {
        return this.status;
    }
    
    public void Account.setStatus(Status status) {
        this.status = status;
    }
    
    public Date Account.getCreatedOn() {
        return this.createdOn;
    }
    
    public void Account.setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }
    
    public Date Account.getLastSignIn() {
        return this.lastSignIn;
    }
    
    public void Account.setLastSignIn(Date lastSignIn) {
        this.lastSignIn = lastSignIn;
    }
    
}

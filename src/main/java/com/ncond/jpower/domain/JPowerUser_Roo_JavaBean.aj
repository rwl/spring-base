// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ncond.jpower.domain;

import com.ncond.jpower.domain.JPowerUser;
import com.ncond.jpower.domain.UserRole;
import com.ncond.jpower.domain.UserStatus;

privileged aspect JPowerUser_Roo_JavaBean {
    
    public void JPowerUser.setUsername(String username) {
        this.username = username;
    }
    
    public void JPowerUser.setPassword(String password) {
        this.password = password;
    }
    
    public UserRole JPowerUser.getUserRole() {
        return this.userRole;
    }
    
    public void JPowerUser.setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }
    
    public String JPowerUser.getFirstName() {
        return this.firstName;
    }
    
    public void JPowerUser.setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String JPowerUser.getLastName() {
        return this.lastName;
    }
    
    public void JPowerUser.setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String JPowerUser.getEmailAddress() {
        return this.emailAddress;
    }
    
    public void JPowerUser.setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    
    public UserStatus JPowerUser.getStatus() {
        return this.status;
    }
    
    public void JPowerUser.setStatus(UserStatus status) {
        this.status = status;
    }
    
}

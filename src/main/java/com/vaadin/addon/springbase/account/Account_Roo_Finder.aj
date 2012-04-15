// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.vaadin.addon.springbase.account;

import com.vaadin.addon.springbase.account.Account;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

privileged aspect Account_Roo_Finder {
    
    public static TypedQuery<Account> Account.findAccountsByUsername(String username) {
        if (username == null || username.length() == 0) throw new IllegalArgumentException("The username argument is required");
        EntityManager em = Account.entityManager();
        TypedQuery<Account> q = em.createQuery("SELECT o FROM Account AS o WHERE o.username = :username", Account.class);
        q.setParameter("username", username);
        return q;
    }
    
}
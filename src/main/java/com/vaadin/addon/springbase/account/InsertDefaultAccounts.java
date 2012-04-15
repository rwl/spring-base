package com.vaadin.addon.springbase.account;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Configurable
public class InsertDefaultAccounts implements ApplicationListener<ContextRefreshedEvent> {

    public static final String PASSWORD = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8";  // "password"

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        init();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void init() {
        if (!Account.findAllAccounts().isEmpty()) { // don't do anything if there is data in the db
            return;
        }

        Account accountAdminActive = new Account();
        accountAdminActive.setUsername("admin");
        accountAdminActive.setPassword(PASSWORD);
        accountAdminActive.setUserRole(Role.ROLE_ADMIN);
        accountAdminActive.setStatus(Status.ACTIVE);
        accountAdminActive.persist();

        Account accountUserActive = new Account();
        accountUserActive.setUsername("user");
        accountUserActive.setPassword(PASSWORD);
        accountUserActive.setUserRole(Role.ROLE_USER);
        accountUserActive.setFirstName("Peter");
        accountUserActive.setLastName("Jones");
        accountUserActive.setStatus(Status.ACTIVE);
        accountUserActive.persist();
    }
}
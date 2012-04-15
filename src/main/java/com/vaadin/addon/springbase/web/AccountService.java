package com.vaadin.addon.springbase.web;

import javax.persistence.TypedQuery;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vaadin.addon.springbase.account.Account;

@Service("userDetailsService")
public class AccountService implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		TypedQuery<Account> query = Account.findAccountsByUsername(username);
		return query.getSingleResult();
	}

}

package com.khajastation.api.security;

import com.khajastation.api.common.Messages;
import com.khajastation.api.common.exception.AccountLockedException;
import com.khajastation.api.entity.User;
import com.khajastation.api.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private Messages messages;

    @Autowired
    private UserService userService;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        if (loginAttemptService.isLocked(email)) {
            throw new AccountLockedException(messages.get("user.account.locked"));
        }
        User user = userService.findByEmail(email);

        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userService.findById(id);

        return UserPrincipal.create(user);
    }
}
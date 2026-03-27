package com.vedavyaas.authenticationservice.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserDetailsRepository userDetailsRepository;

    public UserDetailsServiceImpl(UserDetailsRepository userDetailsRepository) {
        this.userDetailsRepository = userDetailsRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserDetailsEntity> userDetailsEntity = userDetailsRepository.findByUsername(username);

        if (userDetailsEntity.isPresent()) {
            Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(userDetailsEntity.get().getRole().toString()));
            return new User(userDetailsEntity.get().getUsername(), userDetailsEntity.get().getPassword(), authorities);
        } else {
            throw new UsernameNotFoundException("Username not found");
        }
    }
}

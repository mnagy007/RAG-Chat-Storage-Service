package com.ragchat.config.auth;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {
    private final ApiKeyProperties props;

    public ApiKeyUserDetailsService(ApiKeyProperties props) {
        this.props = props;
    }

    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
        String apiKey = (String) token.getPrincipal();
        if (apiKey != null && props.getKeySet().contains(apiKey)) {
            return User.withUsername("api:" + Integer.toHexString(apiKey.hashCode()))
                    .password("N/A").roles("API").build();
        }
        throw new UsernameNotFoundException("Invalid API key");
    }
}

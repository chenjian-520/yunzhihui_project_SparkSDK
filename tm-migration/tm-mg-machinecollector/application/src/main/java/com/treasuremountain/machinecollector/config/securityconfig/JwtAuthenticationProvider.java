package com.treasuremountain.machinecollector.config.securityconfig;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gerrywjzhao on 7/6/2017.
 */
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = ((JwtAuthenticationToken)authentication).getToken();
        List<String> list = new ArrayList<String>() {
            {
                add("ROLE_ADMIN");
            }
        };
        JwtUser user = new JwtUser("Foxconn123", "gerryzhao", list.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()));
        return new JwtAuthenticationToken(user);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return (JwtAuthenticationToken.class.isAssignableFrom(aClass));
    }
}

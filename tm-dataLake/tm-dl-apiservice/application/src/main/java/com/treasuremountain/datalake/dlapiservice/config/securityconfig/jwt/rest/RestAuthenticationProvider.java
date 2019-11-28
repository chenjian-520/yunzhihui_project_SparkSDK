package com.treasuremountain.datalake.dlapiservice.config.securityconfig.jwt.rest;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.SysUserDo;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.user.SysUserImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2018/6/5.
 * Company: Maxnerva
 * Project: polaris
 */
@Component
public class RestAuthenticationProvider implements AuthenticationProvider {
    private final BCryptPasswordEncoder encoder;

    private final SysUserImpl userService;

    @Autowired
    public RestAuthenticationProvider(final SysUserImpl userService, final BCryptPasswordEncoder encoder) {
        this.userService = userService;
        this.encoder = encoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.notNull(authentication, "No authentication data provided");

        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        SysUserDo user = userService.selectByUserAccount(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        if (!user.getIsActive()) {
            throw new DisabledException("User is not active");
        }
        String inputpassword = Hashing.sha256().newHasher().putString(password, Charsets.UTF_8).hash().toString();
        if (!inputpassword.equals(user.getUserPassword())) {
            throw new BadCredentialsException("Authentication Failed. Username or Password not valid.");
        }

        if (user.getUserAuthority() == null) throw new InsufficientAuthenticationException("User has no authority assigned");
        List<String> list = new ArrayList<String>();
        list.add(user.getUserAuthority());
        SecurityUser securityUser = new SecurityUser();
        securityUser.setUserAccount(user.getUserAccount());
        securityUser.setAccountName(user.getUserName());
        securityUser.setPassword(user.getUserPassword());
        securityUser.setUserId(user.getId());
        securityUser.setAuthority(list.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()));

        return new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}

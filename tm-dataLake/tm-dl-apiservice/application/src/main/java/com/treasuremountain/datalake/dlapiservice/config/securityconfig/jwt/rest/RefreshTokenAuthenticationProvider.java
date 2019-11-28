package com.treasuremountain.datalake.dlapiservice.config.securityconfig.jwt.rest;

import com.treasuremountain.datalake.dlapiservice.config.securityconfig.jwt.token.JwtTokenFactory;
import com.treasuremountain.datalake.dlapiservice.config.securityconfig.jwt.token.RawAccessJwtToken;
import com.treasuremountain.datalake.dlapiservice.config.securityconfig.jwt.token.RefreshAuthenticationToken;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.model.SysUserDo;
import com.treasuremountain.datalake.dlapiservice.dao.mysql.user.SysUserImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
public class RefreshTokenAuthenticationProvider implements AuthenticationProvider {

    private final JwtTokenFactory tokenFactory;
    private final SysUserImpl userService;

    @Autowired
    public RefreshTokenAuthenticationProvider(final SysUserImpl userService, final JwtTokenFactory tokenFactory) {
        this.userService = userService;
        this.tokenFactory = tokenFactory;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.notNull(authentication, "No authentication data provided");
        RawAccessJwtToken rawAccessToken = (RawAccessJwtToken) authentication.getCredentials();
        SecurityUser unsafeUser = tokenFactory.parseRefreshToken(rawAccessToken);

        SysUserDo user = userService.selectByUserAccount(unsafeUser.getUserAccount());
        if (user == null) {
            throw new UsernameNotFoundException("User not found by refresh token");
        }

        if (!user.getIsActive()) {
            throw new DisabledException("User is not active");
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

        return new RefreshAuthenticationToken(securityUser);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (RefreshAuthenticationToken.class.isAssignableFrom(authentication));
    }
}

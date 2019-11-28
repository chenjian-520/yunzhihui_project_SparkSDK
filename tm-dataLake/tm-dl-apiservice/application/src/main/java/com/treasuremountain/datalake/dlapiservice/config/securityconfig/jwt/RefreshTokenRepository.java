package com.treasuremountain.datalake.dlapiservice.config.securityconfig.jwt;

import com.treasuremountain.datalake.dlapiservice.config.securityconfig.jwt.rest.SecurityUser;
import com.treasuremountain.datalake.dlapiservice.config.securityconfig.jwt.token.JwtToken;
import com.treasuremountain.datalake.dlapiservice.config.securityconfig.jwt.token.JwtTokenFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2018/6/5.
 * Company: Maxnerva
 * Project: polaris
 */
@Component
public class RefreshTokenRepository {

    private final JwtTokenFactory tokenFactory;

    @Autowired
    public RefreshTokenRepository(final JwtTokenFactory tokenFactory) {
        this.tokenFactory = tokenFactory;
    }

    public JwtToken requestRefreshToken(SecurityUser user) {
        return tokenFactory.createRefreshToken(user);
    }

}

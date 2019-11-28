package com.treasuremountain.datalake.dlapiservice.config.securityconfig.jwt.token;

import com.treasuremountain.datalake.dlapiservice.config.securityconfig.JwtSettings;
import com.treasuremountain.datalake.dlapiservice.config.securityconfig.jwt.Authority;
import com.treasuremountain.datalake.dlapiservice.config.securityconfig.jwt.rest.SecurityUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2018/6/5.
 * Company: Maxnerva
 * Project: polaris
 */
@Component
public class JwtTokenFactory {

    private static final String SCOPES = "scopes";
    private static final String USER_ID = "userId";
    private static final String USER_NAME = "userName";
    private static final String USER_ACCOUNT = "useraccount";
    private static final String LAST_NAME = "lastName";
    private static final String ENABLED = "enabled";
    private static final String TENANT_ID = "tenantId";
    private static final String CUSTOMER_ID = "customerId";
    private static final String AUTH_CODE= "authCode";
    private static final String CREATED ="created";
    private static final String ACCOUNT="account";
    private static final String ACCOUNT_NAME = "accountname";
    private static final String PASSWORD="password";

    private final JwtSettings settings;

    @Autowired
    public JwtTokenFactory(JwtSettings settings) {
        this.settings = settings;
    }

    /**
     * Factory method for issuing new JWT Tokens.
     */
    public AccessJwtToken createAccessJwtToken(SecurityUser securityUser) {
        if (StringUtils.isBlank(securityUser.getUsername()))
            throw new IllegalArgumentException("Cannot create JWT Token without username/email");

        if (securityUser.getAuthorities() == null){
            throw new IllegalArgumentException("User doesn't have any privileges");
        }
        Claims claims = Jwts.claims().setSubject(securityUser.getUsername());
        claims.put(SCOPES, securityUser.getAuthorities().stream().map(s -> s.getAuthority()).collect(Collectors.toList()));
        claims.put(USER_ID, securityUser.getUserId());
        claims.put(ACCOUNT_NAME, securityUser.getAccountName());
        claims.put(ACCOUNT,securityUser.getUserAccount());
        claims.put(ENABLED, securityUser.isEnabled());
        claims.put(CREATED,new Date());

        DateTime currentTime = new DateTime();

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(settings.getTokenIssuer())
                .setIssuedAt(currentTime.toDate())
                .setExpiration(currentTime.plusSeconds(settings.getTokenExpirationTime()).toDate())
                .signWith(SignatureAlgorithm.HS512, settings.getTokenSigningKey())
                .compact();

        return new AccessJwtToken(token, claims);

    }

    public SecurityUser parseAccessJwtToken(RawAccessJwtToken rawAccessToken) {
        Jws<Claims> jwsClaims = rawAccessToken.parseClaims(settings.getTokenSigningKey());
        Claims claims = jwsClaims.getBody();
        String subject = claims.getSubject();
        List<String> scopes = claims.get(SCOPES, List.class);
        if (scopes == null || scopes.isEmpty()) {
            throw new IllegalArgumentException("JWT Token doesn't have any scopes");
        }

        SecurityUser jwtUser = new SecurityUser();
        jwtUser.setUserAccount(claims.get(ACCOUNT, String.class));
        jwtUser.setEnabled(claims.get(ENABLED, Boolean.class));
        jwtUser.setAccountName(claims.get(ACCOUNT_NAME, String.class));
        jwtUser.setUserId(claims.get(USER_ID, String.class));
        jwtUser.setAuthority(scopes.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()));
        return jwtUser;
    }

    public JwtToken createRefreshToken(SecurityUser securityUser) {
        if (StringUtils.isBlank(securityUser.getUsername())) {
            throw new IllegalArgumentException("Cannot create JWT Token without username/email");
        }
        DateTime currentTime = new DateTime();

        Claims claims = Jwts.claims().setSubject(securityUser.getUsername());
        claims.put(SCOPES, Arrays.asList(Authority.REFRESH_TOKEN.name()));
        claims.put(USER_ID, securityUser.getUserId());
        claims.put(ACCOUNT_NAME, securityUser.getAccountName());
        claims.put(ACCOUNT,securityUser.getUserAccount());
        claims.put(ENABLED, securityUser.isEnabled());
        claims.put(PASSWORD,securityUser.getPassword());
        claims.put(CREATED,new Date());

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(settings.getTokenIssuer())
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(currentTime.toDate())
                .setExpiration(currentTime.plusSeconds(settings.getRefreshTokenExpTime()).toDate())
                .signWith(SignatureAlgorithm.HS512, settings.getTokenSigningKey())
                .compact();

        return new AccessJwtToken(token, claims);
    }

    public SecurityUser parseRefreshToken(RawAccessJwtToken rawAccessToken) {
        Jws<Claims> jwsClaims = rawAccessToken.parseClaims(settings.getTokenSigningKey());
        Claims claims = jwsClaims.getBody();
        String subject = claims.getSubject();
        List<String> scopes = claims.get(SCOPES, List.class);
        if (scopes == null || scopes.isEmpty()) {
            throw new IllegalArgumentException("Refresh Token doesn't have any scopes");
        }
        if (!scopes.get(0).equals(Authority.REFRESH_TOKEN.name())) {
            throw new IllegalArgumentException("Invalid Refresh Token scope");
        }
        Date createDt=claims.get(CREATED, Date.class);

        SecurityUser jwtUser = new SecurityUser();
        jwtUser.setUserAccount(claims.get(ACCOUNT,String.class));
        jwtUser.setPassword(claims.get(PASSWORD,String.class));
        jwtUser.setEnabled(claims.get(ENABLED, Boolean.class));
        jwtUser.setAuthority(scopes.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()));
        return jwtUser;
    }


}

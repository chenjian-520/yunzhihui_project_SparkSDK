package com.treasuremountain.datalake.dlapiservice.config.securityconfig;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2018/6/6.
 * Company: Maxnerva
 * Project: polaris
 */
public class CustomCorsFilter extends CorsFilter {

    public CustomCorsFilter() {
        super(configurationSource());
    }

    private static UrlBasedCorsConfigurationSource configurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        HashMap<String, CorsConfiguration> CorsHashMap = new HashMap<>();
        try {
            config.setAllowCredentials(true);
            config.addAllowedOrigin("*");
            config.addAllowedHeader("*");
            config.setMaxAge(36000L);
            config.setAllowedMethods(Arrays.asList("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS"));
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            CorsHashMap.putIfAbsent("/api/**", config);
            source.setCorsConfigurations(CorsHashMap);
            return source;
        } finally {
            config = null;
            CorsHashMap = null;
        }
    }
}

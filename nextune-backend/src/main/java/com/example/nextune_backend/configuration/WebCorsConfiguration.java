package com.example.nextune_backend.configuration;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class WebCorsConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(WebCorsConfiguration.class);

    @Value("${web.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Value("${web.cors.allowed-headers}")
    private List<String> allowedHeaders;

    @Value("${web.cors.exposed-headers}")
    private List<String> exposedHeaders;

    @Value("${web.cors.allowed-methods}")
    private List<String> allowedMethods;

    @Value("${web.cors.allowed-credentials}")
    private Boolean allowedCredentials;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        logger.info("CORS Configuration - Allowed Origins: {}", allowedOrigins);
        logger.info("CORS Configuration - Allowed Headers: {}", allowedHeaders);
        logger.info("CORS Configuration - Allowed Methods: {}", allowedMethods);
        logger.info("CORS Configuration - Allow Credentials: {}", allowedCredentials);
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(allowedOrigins);
        corsConfig.setAllowedHeaders(allowedHeaders);
        corsConfig.setAllowedMethods(allowedMethods);
        corsConfig.setAllowCredentials(allowedCredentials);
        corsConfig.setExposedHeaders(exposedHeaders);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return source;
    }
}
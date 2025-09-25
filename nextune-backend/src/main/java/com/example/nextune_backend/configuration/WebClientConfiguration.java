package com.example.nextune_backend.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {
    @Bean
    public WebClient nluClient(@Value("${voice.nluUrl}") String nluUrl) {
        return WebClient.builder().baseUrl(nluUrl).build();
    }
}
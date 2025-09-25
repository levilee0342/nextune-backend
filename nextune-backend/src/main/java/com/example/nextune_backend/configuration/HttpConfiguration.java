package com.example.nextune_backend.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class HttpConfiguration  {
    @Value("${recommend.url}") String recUrl;
    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl(recUrl).build();
    }
}

package com.example.nextune_backend.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.nextune_backend.repository.UserRepository;
import com.example.nextune_backend.security.AuthenticationFilter;
import com.example.nextune_backend.security.ForbiddenEntryPoint;
import com.example.nextune_backend.security.UnauthorizedEntryPoint;
import com.example.nextune_backend.security.UserPrincipal;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
@ComponentScan(basePackageClasses = {
        UserPrincipal.class, UserRepository.class})
public class SecurityConfiuration {

    private final UserPrincipal userPrincipal;
    private final AuthenticationFilter authFilter;
    private final UnauthorizedEntryPoint unauthorizedEntryPoint;
    private final ForbiddenEntryPoint forbiddenEntryPoint;
    private final WebCorsConfiguration webCorsConfiguration;
    public PasswordEncoder bryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userPrincipal).passwordEncoder(bryptPasswordEncoder());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(webCorsConfiguration.corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(unauthorizedEntryPoint) // 401
                        .accessDeniedHandler(forbiddenEntryPoint) // 403
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/otp/**",
                                "/swagger-resources/**",
                                "/webjars/**")
                            .permitAll()
                        .requestMatchers("/genres/all")
                            .permitAll()
                        .requestMatchers("/api/admin/**",
                                "/genres/**")
                            .hasAuthority("ADMIN")
                        .requestMatchers("/albums/**")
                            .hasAnyAuthority("ADMIN","ARTIST")

                        .anyRequest()
                            .authenticated()
                )
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


}

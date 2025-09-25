package com.example.nextune_backend.configuration;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.stereotype.Component;

import com.example.nextune_backend.repository.UserRepository;
import com.example.nextune_backend.security.AuthenticationFilter;
import com.example.nextune_backend.security.ForbiddenEntryPoint;
import com.example.nextune_backend.security.UnauthorizedEntryPoint;
import com.example.nextune_backend.security.UserPrincipal;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

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
                .cors(c -> c.configurationSource(webCorsConfiguration.corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(unauthorizedEntryPoint)
                        .accessDeniedHandler(forbiddenEntryPoint)
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // 0) CORS preflight and WS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/voice/**","/stomp/**","/ws/**").permitAll()
                        .requestMatchers("/topic/**","/app/**").permitAll()

                        // 1) Tài liệu/Swagger & OTP & callback thanh toán
                        .requestMatchers(
                                "/swagger-ui/**", "/v3/api-docs/**",
                                "/swagger-resources/**", "/webjars/**"
                        ).permitAll()
                        .requestMatchers(
                                "/billing/vnpay/ipn"        // GET/POST đều public cho IPN
                        ).permitAll()
                        .requestMatchers(
                                "/payment/vnpay/return"
                        ).permitAll()

                        // 2) Auth: chỉ mở những endpoint thực sự cần public
                        .requestMatchers(
                                HttpMethod.GET,
                                "/auth/login/google/**"
                        ).permitAll()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/auth/register", "/auth/login",
                                "/auth/login-with-otp", "/auth/password/forgot",
                                "/auth/password/reset", "/auth/refresh","/otp/**"
                        ).permitAll()
                        // Các endpoint còn lại dưới /auth/** (ví dụ /auth/me, /auth/logout) yêu cầu đăng nhập
                        .requestMatchers("/auth/**").authenticated()

                        // 3) ADMIN trước (chỉ những thao tác quản trị)
                        // Viết/ sửa/ xóa trên các tài nguyên quản trị
                        .requestMatchers(HttpMethod.POST,
                                "/genres/**","/tracks","/track-artists/**",
                                "/albums/**","/user-genre/**","/track-genre/**",
                                "/playlist-genre/**","/album-genre/**","/reports/**",
                                "/cloudinary/**", "/subscriptions/**", "/notifications/**"
                        ).hasAnyAuthority("ADMIN", "ARTIST")
                        .requestMatchers(HttpMethod.PUT,
                                "/genres/**","/tracks/**","/track-artists/**",
                                "/albums/**","/user-genre/**","/track-genre/**",
                                "/playlist-genre/**","/album-genre/**","/reports/**",
                                "/cloudinary/**", "/subscriptions/**","/notifications/**"
                        ).hasAnyAuthority("ADMIN", "ARTIST")
                        .requestMatchers(HttpMethod.DELETE,
                                "/genres/**","/tracks/**","/track-artists/**",
                                "/albums/**","/user-genre/**","/track-genre/**",
                                "/playlist-genre/**","/album-genre/**","/reports/**",
                                "/cloudinary/**", "/subscriptions/**","/notifications/**"
                        ).hasAnyAuthority("ADMIN", "ARTIST")
                        .requestMatchers(HttpMethod.PATCH,
                                "/genres/**","/tracks/**","/track-artists/**",
                                "/albums/**","/user-genre/**","/track-genre/**",
                                "/playlist-genre/**","/album-genre/**","/reports/**",
                                "/cloudinary/**", "/subscriptions/**","/notifications/**"
                        ).hasAnyAuthority("ADMIN", "ARTIST")

                        // 4) User đã đăng nhập (write + dữ liệu cá nhân)
                        .requestMatchers(
                                HttpMethod.POST,
                                "/reports","/playlists","/comments",
                                "/track-collections","/playlist-saves",
                                "/follow/**","/billing/purchase",
                                "/tracks/play/**",           // play count/event
                                "/cloudinary/users/**","/notifications/**"       // đổi avatar/cover user
                        ).authenticated()
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/profile/**","/playlists","/comments","/notifications/**"
                        ).authenticated()
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/playlists","/track-collections/**",
                                "/playlist-saves","/cloudinary/users/**","/notifications/**",
                                "/comments/**"
                        ).authenticated()
                        .requestMatchers(
                                HttpMethod.GET,
                                "/playlist-saves/**","/payment/**",
                                "/follow/**","/profile", "/subscriptions/**","/notifications/**" // /profile (me) nên yêu cầu token
                        ).authenticated()

                        // 5) Public READ-ONLY (browse nội dung)
                        .requestMatchers(HttpMethod.GET,
                                "/tracks/**","/track-artists/**",
                                "/playlists/**","/albums/**",
                                "/genres/**","/comments/track/**",
                                "/search","/tracks/all","/playlists/all","/albums/all","/genres/all","/notifications/**"
                        ).permitAll()

                        // 6) Mặc định
                        .anyRequest().authenticated()
                )
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Component
    public class RemoveCorsHeaderFilter implements Filter {

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {

            chain.doFilter(request, response);

//            if (response instanceof HttpServletResponse) {
//                HttpServletResponse res = (HttpServletResponse) response;
//                res.setHeader("Access-Control-Allow-Origin", null);
//                res.setHeader("Access-Control-Allow-Credentials", null);
//            }
        }
    }

}

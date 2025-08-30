package com.example.nextune_backend.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final UserPrincipal userPrincipal;
    private final HandlerExceptionResolver globalResolver;

    public AuthenticationFilter(
            TokenProvider tokenProvider,
            UserPrincipal userPrincipal,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver globalResolver
    ) {
        this.tokenProvider = tokenProvider;
        this.userPrincipal = userPrincipal;
        this.globalResolver = globalResolver;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            // Lấy token từ header Authorization
            //String authHeader = request.getHeader("Authorization");
            String token = null;
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }


if (token == null) {
    if (request.getCookies() != null) {
        for (Cookie cookie : request.getCookies()) {
            if ("ACCESS_TOKEN".equals(cookie.getName())) {
                token = cookie.getValue();
            }
        }
    }
}

// 3. Validate & set SecurityContext
if (token != null && tokenProvider.validateToken(token)) {
    String username = tokenProvider.getSubject(token);
    var userDetails = userPrincipal.loadUserByUsername(username);

    UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

    SecurityContextHolder.getContext().setAuthentication(authentication);
}

        } catch (AuthenticationException ex) { // handle 401
            SecurityContextHolder.clearContext();
            log.error("Unauthorized error", ex);
        } catch (AccessDeniedException ex) { // handle 403
            SecurityContextHolder.clearContext();
            log.error("Access denied error", ex);
        } catch (Exception ex) { // handle another exceptions
            SecurityContextHolder.clearContext();
            globalResolver.resolveException(request, response, null, ex);
        }

        filterChain.doFilter(request, response);
    }
}

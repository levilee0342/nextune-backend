package com.example.nextune_backend.configuration;

import com.example.nextune_backend.security.TokenProvider;
import com.example.nextune_backend.security.UserPrincipal;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class CookieJwtHandshakeInterceptor implements HandshakeInterceptor {

    private final TokenProvider tokenProvider;
    private final UserPrincipal userPrincipal;

    public CookieJwtHandshakeInterceptor(TokenProvider tokenProvider, UserPrincipal userPrincipal) {
        this.tokenProvider = tokenProvider;
        this.userPrincipal = userPrincipal;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest sr) {
            var http = sr.getServletRequest();
            var cookies = http.getCookies();
            if (cookies != null) {
                for (var c : cookies) {
                    if ("ACCESS_TOKEN".equals(c.getName())) {
                        var token = c.getValue();
                        if (token != null && tokenProvider.validateToken(token)) {
                            var username = tokenProvider.getSubject(token);
                            var userDetails = userPrincipal.loadUserByUsername(username) ;
                            var auth = new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                            attributes.put("auth", auth);
                            attributes.put("principal", auth); // dùng làm Principal
                            return true;
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override public void afterHandshake(ServerHttpRequest r, ServerHttpResponse s,
                                         WebSocketHandler h, Exception ex) {}
}

package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.service.GoogleTokenService;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleTokenServiceImpl implements GoogleTokenService {
    private final WebClient webClient = WebClient.builder().build();

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    // 3.1) Đổi authorization code -> token (lấy id_token)
    public Map<String, Object> exchangeCode(String code) {
        return webClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .bodyValue(Map.of(
                        "code", code,
                        "client_id", clientId,
                        "client_secret", clientSecret,
                        "redirect_uri", redirectUri,
                        "grant_type", "authorization_code"))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String,Object>>() {})
                .block();
    }

    // 3.2) Verify id_token bằng JWKS Google (Nimbus)
    public JWTClaimsSet verifyIdToken(String idToken) {
        try {
            var jwt = SignedJWT.parse(idToken);

            // JWKS của Google (Nimbus tự cache qua DefaultJWKSetCache nếu dùng RemoteJWKSet)
            var jwkSource = new RemoteJWKSet<SecurityContext>(
                    new URL("https://www.googleapis.com/oauth2/v3/certs"),
                    new DefaultResourceRetriever(3000, 3000, 1024*1024));

            var keySelector = new JWSVerificationKeySelector<SecurityContext>(JWSAlgorithm.RS256, jwkSource);
            var processor = new DefaultJWTProcessor<SecurityContext>();
            processor.setJWSKeySelector(keySelector);

            // enforce issuer & audience
            var expected = new JWTClaimsSet.Builder()
                    .issuer("https://accounts.google.com")
                    .audience(clientId)
                    .build();
            var required = new HashSet<String>(Arrays.asList("sub","exp","iat"));
            processor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<>(expected, required));

            return processor.process(jwt, null); // hợp lệ -> trả claims
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google ID token", e);
        }
    }
}

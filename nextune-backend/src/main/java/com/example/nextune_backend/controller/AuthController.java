package com.example.nextune_backend.controller;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.nextune_backend.dto.GoogleUserProfile;
import com.example.nextune_backend.dto.request.ForgotPasswordRequest;
import com.example.nextune_backend.dto.request.LoginRequest;
import com.example.nextune_backend.dto.request.OtpLoginRequest;
import com.example.nextune_backend.dto.request.RegisterRequest;
import com.example.nextune_backend.dto.request.ResetPasswordRequest;
import com.example.nextune_backend.dto.response.LoginResponse;
import com.example.nextune_backend.dto.response.RefreshTokenResponse;
import com.example.nextune_backend.dto.response.RegisterResponse;
import com.example.nextune_backend.dto.response.UserInfoResponse;
import com.example.nextune_backend.entity.RefreshToken; // <-- thêm service này
import com.example.nextune_backend.entity.User;
import com.example.nextune_backend.security.TokenProvider;
import com.example.nextune_backend.service.AuthService;
import com.example.nextune_backend.service.GoogleTokenService;
import com.example.nextune_backend.service.RefreshTokenService;
import com.example.nextune_backend.service.UserService;
import com.nimbusds.jwt.JWTClaimsSet;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Value("${app.jwt.access-expires-in-min:10}")
    private long accessExpMin;

    @Value("${app.jwt.session-expires-in-days:7}")
    private long sessionExpDays;

    // --- Google OAuth config ---
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    private final AuthService authService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    private final GoogleTokenService googleTokenService; // <-- inject

    // ---------- Auth cơ bản ----------
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse result = authService.register(request);
        return ResponseEntity
                .created(URI.create("/users/" + result.getUserId()))
                .body(result);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpServletRequest) {
        LoginResponse res = authService.login(request, httpServletRequest);

        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", res.getAccessToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(accessExpMin * 60)
                .build();

        ResponseCookie sessionCookie = ResponseCookie.from("SESSION_TOKEN", res.getSessionToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(sessionExpDays * 60 * 60 * 24)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, sessionCookie.toString()).body(res);

    }

    @PostMapping("/login-with-otp")
    public ResponseEntity<LoginResponse> loginWithOtp(@Valid @RequestBody OtpLoginRequest request, HttpServletRequest httpServletRequest) {

        LoginResponse res = authService.loginWithOtp(request, httpServletRequest);

        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", res.getAccessToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(accessExpMin * 60)
                .build();

        ResponseCookie sessionCookie = ResponseCookie.from("SESSION_TOKEN", res.getSessionToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(sessionExpDays * 24 * 60 * 60)
                .build();

        System.out.println("Access cookie: " + accessCookie);
        System.out.println("Session cookie: " + sessionCookie);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, sessionCookie.toString())
                .body(res);
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<Void> forgot(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.requestPasswordResetOtp(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Void> reset(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPasswordWithOtp(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest servletReq) {
        return authService.refreshSession(servletReq);
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> checkAuth(HttpServletRequest servletReq) {
        return authService.checkAuth(servletReq);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.noContent().build();
    }

    // ---------- Google OAuth ----------
    @GetMapping("/login/google")
    public void redirectToGoogle(HttpServletResponse resp) throws IOException {
        String url = UriComponentsBuilder
                .fromUriString("https://accounts.google.com/o/oauth2/v2/auth")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "openid email profile")
                .queryParam("access_type", "offline") // refresh_token lần đầu
                .queryParam("prompt", "consent") // ép consent
                // .queryParam("state", generateStateAndSetCookie(...)) // khuyến nghị: thêm state
                .build().toUriString();
        resp.sendRedirect(url);
    }

    @GetMapping("/login/google/callback")
    public ResponseEntity<?> googleCallback(@RequestParam String code, HttpServletRequest req) throws ParseException {
        Map<String, Object> tokenRes = googleTokenService.exchangeCode(code);
        String idToken = (String) tokenRes.get("id_token");

        JWTClaimsSet claims = googleTokenService.verifyIdToken(idToken);
        GoogleUserProfile g = toProfile(claims);

        User user = userService.upsertGoogleUser(g);
        String principal = Optional.ofNullable(user.getEmail())
                .filter(s -> !s.isBlank())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User email is missing"));

        UserDetails ud = userDetailsService.loadUserByUsername(principal);
        Authentication auth = new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());

        String access  = tokenProvider.generateAccessToken(auth);
        String refresh = tokenProvider.generateRefreshToken(principal);

        // NEW: tạo session token riêng
        String raw = UUID.randomUUID()+":"+principal+":"+System.currentTimeMillis();
        String sessionToken = tokenProvider.generateSessionToken(raw);

        // Lưu refresh kèm session
        refreshTokenService.store(
                refresh,
                principal,
                req.getHeader("User-Agent"),
                req.getRemoteAddr(),
                tokenProvider.getExpiration(refresh),
                sessionToken
        );

        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", access)
                .httpOnly(true).secure(true).sameSite("None").path("/")
                .maxAge(Duration.ofMinutes(accessExpMin)).build();

        ResponseCookie sessionCookie = ResponseCookie.from("SESSION_TOKEN", sessionToken) // <-- đúng
                .httpOnly(true).secure(true).sameSite("None").path("/")
                .maxAge(Duration.ofDays(sessionExpDays)).build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, sessionCookie.toString())
                .body(Map.of(
                        "access_token", access,
                        "session_token", sessionToken,
                        "token_type", "Bearer",
                        "expires_in", accessExpMin * 60
                ));
    }


    // --- Helper: map claims -> GoogleUserProfile ---
    private GoogleUserProfile toProfile(JWTClaimsSet c) throws ParseException {
        return new GoogleUserProfile(
                safeClaim(c, "sub"),
                safeClaim(c, "email"),
                c.getBooleanClaim("email_verified") != null ? c.getBooleanClaim("email_verified") : Boolean.FALSE,
                safeClaim(c, "name"),
                safeClaim(c, "picture")
        );
    }

    private String safeClaim(JWTClaimsSet c, String name) {
        try {
            return c.getStringClaim(name);
        } catch (Exception e) {
            return null;
        }
    }
}

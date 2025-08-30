package com.example.nextune_backend.controller;

import java.net.URI;
import java.util.Arrays;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.nextune_backend.dto.request.ForgotPasswordRequest;
import com.example.nextune_backend.dto.request.LoginRequest;
import com.example.nextune_backend.dto.request.OtpLoginRequest;
import com.example.nextune_backend.dto.request.RegisterRequest;
import com.example.nextune_backend.dto.request.ResetPasswordRequest;
import com.example.nextune_backend.dto.response.LoginResponse;
import com.example.nextune_backend.dto.response.RefreshTokenResponse;
import com.example.nextune_backend.dto.response.RegisterResponse;
import com.example.nextune_backend.dto.response.UserInfoResponse;
import com.example.nextune_backend.entity.RefreshToken;
import com.example.nextune_backend.entity.User;
import com.example.nextune_backend.security.TokenProvider;
import com.example.nextune_backend.service.AuthService;
import com.example.nextune_backend.service.RefreshTokenService;

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



    private final AuthService authService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse result = authService.register(request);
        return ResponseEntity
                .created(URI.create("/users/" + result.getUserId()))
                .body(result);
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpServletRequest) {
         LoginResponse res = authService.login(request, httpServletRequest);

    ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", res.getAccessToken())
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(accessExpMin * 60 )
            .build();

    ResponseCookie sessionCookie = ResponseCookie.from("SESSION_TOKEN", res.getSessionToken())
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(sessionExpDays * 60 * 60 * 24 )
            .build();

      return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, sessionCookie.toString()).build();
            
    }

    @PostMapping("/login-with-otp")
    public ResponseEntity<LoginResponse> loginWithOtp(@Valid @RequestBody OtpLoginRequest request, HttpServletRequest httpServletRequest) {

        LoginResponse res = authService.loginWithOtp(request, httpServletRequest);

        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", res.getAccessToken())
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
                   .maxAge(accessExpMin * 60 )
            .build();

        ResponseCookie sessionCookie = ResponseCookie.from("SESSION_TOKEN", res.getSessionToken())
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(sessionExpDays * 24 * 60 * 50 )
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
    try {
        String rawSessionToken = Arrays.stream(Optional.ofNullable(servletReq.getCookies()).orElse(new Cookie[0]))
            .filter(c -> "SESSION_TOKEN".equals(c.getName()))
            .findFirst()
            .map(Cookie::getValue)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session token missing"));

        RefreshToken stored = refreshTokenService.findActiveByRawSessionToken(rawSessionToken)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid session token"));

       
        refreshTokenService.revoke(stored);

        User user = stored.getUser();
        UserDetails ud = userDetailsService.loadUserByUsername(user.getName());
        Authentication auth = new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());

        String newAccess = tokenProvider.generateAccessToken(auth);
        String newRefresh = tokenProvider.generateRefreshToken(user.getName());

        if (stored == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid session token");
        }

    
        refreshTokenService.store(
            newRefresh,
            user.getName(),
            servletReq.getHeader("User-Agent"),
            servletReq.getRemoteAddr(),
            tokenProvider.getExpiration(newRefresh),
            rawSessionToken
        );


        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", newAccess)
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
                        .maxAge(accessExpMin * 60 )
            .build();


        ResponseCookie sessionCookie = ResponseCookie.from("SESSION_TOKEN", newRefresh)
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
                .maxAge(sessionExpDays * 24 * 60 * 50 )
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
            .header(HttpHeaders.SET_COOKIE, sessionCookie.toString())
            .body(new RefreshTokenResponse(newAccess, newRefresh, sessionCookie.getValue()));

    } catch (ResponseStatusException e) {
        
        return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
    }
}


@GetMapping("/me")
public ResponseEntity<UserInfoResponse> checkAuth(HttpServletRequest servletReq) {
    String rawSessionToken = Arrays.stream(
            Optional.ofNullable(servletReq.getCookies()).orElse(new Cookie[0]))
        .filter(c -> "SESSION_TOKEN".equals(c.getName()))
        .findFirst()
        .map(Cookie::getValue)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

    RefreshToken stored = refreshTokenService.findActiveByRawSessionToken(rawSessionToken)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

    User user = stored.getUser();

    UserInfoResponse userInfo = new UserInfoResponse(
            user.getId(),
            user.getAvatar(),
            user.getEmail()
    );

    System.out.println(userInfo);

    return ResponseEntity.ok(userInfo);
}

   @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.noContent().build(); 
    }



}

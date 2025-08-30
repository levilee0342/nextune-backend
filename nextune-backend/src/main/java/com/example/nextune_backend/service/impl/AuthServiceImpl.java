package com.example.nextune_backend.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.nextune_backend.dto.request.ForgotPasswordRequest;
import com.example.nextune_backend.dto.request.LoginRequest;
import com.example.nextune_backend.dto.request.OtpLoginRequest;
import com.example.nextune_backend.dto.request.OtpPurpose;
import com.example.nextune_backend.dto.request.RegisterRequest;
import com.example.nextune_backend.dto.request.ResetPasswordRequest;
import com.example.nextune_backend.dto.response.LoginResponse;
import com.example.nextune_backend.dto.response.RegisterResponse;
import com.example.nextune_backend.entity.Role;
import com.example.nextune_backend.entity.User;
import com.example.nextune_backend.mapper.RegisterUserMapper;
import com.example.nextune_backend.repository.OtpTokenRepository;
import com.example.nextune_backend.repository.RoleRepository;
import com.example.nextune_backend.repository.UserRepository;
import com.example.nextune_backend.security.TokenProvider;
import com.example.nextune_backend.service.AuthService;
import com.example.nextune_backend.service.OtpService;
import com.example.nextune_backend.service.RefreshTokenService;
import com.example.nextune_backend.utility.PasswordEncoderUtility;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OtpTokenRepository otpTokenRepository;

    private final TokenProvider tokenProvider;
    private final RegisterUserMapper registerUserMapper;
    private final OtpService otpService;
    private final AuthenticationManager authenticationManager;

    private final RefreshTokenService refreshTokenService;
    private Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    @Override
    public RegisterResponse register(RegisterRequest request) {
        // Validate email định dạng hợp lệ
        if (request.getEmail() == null || !EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 6 characters");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));

        User user = registerUserMapper.map(request);
        user.setPassword(PasswordEncoderUtility.encodePassword(user.getPassword()));
        user.setRole(role);

        user = userRepository.save(user);

        return new RegisterResponse(user.getEmail(), user.getId(), user.getName());
    }

    @Override
    public LoginResponse login(LoginRequest request, HttpServletRequest httpServletRequest) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!PasswordEncoderUtility.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        if (!request.getRoleId().trim().equalsIgnoreCase(user.getRole().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String raw = UUID.randomUUID().toString() + ":" + user.getEmail() + ":" + System.currentTimeMillis();
        String sessionToken = tokenProvider.generateSessionToken(raw);

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(user.getEmail());

        refreshTokenService.store(
                refreshToken,
                user.getEmail(),
                httpServletRequest.getHeader("User-Agent"),
                httpServletRequest.getRemoteAddr(),
                tokenProvider.getExpiration(refreshToken),
                sessionToken
        );

        System.out.println(accessToken+" "+ user.getId()+ " "+user.getEmail()+ " "+user.getAvatar());
        return new LoginResponse(accessToken, refreshToken, sessionToken ,user.getId(), user.getEmail(), user.getAvatar());
    }

    @Override
    public LoginResponse loginWithOtp(OtpLoginRequest request , HttpServletRequest httpServletRequest) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or otp"));


        if (!otpService.verifyAndConsume(request.getEmail(), request.getOtp(), OtpPurpose.LOGIN)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or otp");
        }

        if (!request.getRoleId().trim().equalsIgnoreCase(user.getRole().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role");
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        null,
                        List.of(new SimpleGrantedAuthority(user.getRole().getName().name()))
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(user.getEmail());

        String raw = UUID.randomUUID().toString() + ":" + user.getEmail() + ":" + System.currentTimeMillis();
        String sessionToken = tokenProvider.generateSessionToken(raw);

        refreshTokenService.store(
                refreshToken,
                user.getEmail(),
                httpServletRequest.getHeader("User-Agent"),
                httpServletRequest.getRemoteAddr(),
                tokenProvider.getExpiration(refreshToken),
                sessionToken
        );


        System.out.println(accessToken+" "+ user.getId()+ " "+user.getEmail()+ " "+user.getAvatar());
        return new LoginResponse(accessToken, sessionToken, sessionToken ,user.getId(), user.getEmail(), user.getAvatar());
    }

    @Override
    public void requestPasswordResetOtp(ForgotPasswordRequest request) {
        if (request.email() == null || !EMAIL_PATTERN.matcher(request.email()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format");
        }
        if (request.newPassword() == null || request.newPassword().length() < 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 8 characters");
        }
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }



        userRepository.findByEmail(request.email()).ifPresent(u ->
                otpService.requestOtp(request.email(), OtpPurpose.RESET_PASSWORD)
        );
    }

    @Override
    public void resetPasswordWithOtp(ResetPasswordRequest request) {
        if (request.email() == null || !EMAIL_PATTERN.matcher(request.email()).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email or OTP"));


        otpService.verifyAndConsume(request.email(), request.otp(), OtpPurpose.RESET_PASSWORD);
        
        

     
        user.setPassword(PasswordEncoderUtility.encodePassword(request.newPassword()));

        userRepository.save(user);

 
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
       
        String sessionToken = null;
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("SESSION_TOKEN".equals(c.getName())) {
                    sessionToken = c.getValue();
                }
            }
        }

        // if (sessionToken != null) {
        //     refreshTokenService.deleteByRawSessionToken(sessionToken);
        // }

      
        ResponseCookie clearAccessCookie = ResponseCookie.from("ACCESS_TOKEN", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie clearSessionCookie = ResponseCookie.from("SESSION_TOKEN", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", clearAccessCookie.toString());
        response.addHeader("Set-Cookie", clearSessionCookie.toString());
    }


}

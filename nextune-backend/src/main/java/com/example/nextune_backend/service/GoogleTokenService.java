package com.example.nextune_backend.service;

import com.nimbusds.jwt.JWTClaimsSet;

import java.util.Map;

public interface GoogleTokenService {
    Map<String, Object> exchangeCode(String code);
    JWTClaimsSet verifyIdToken(String idToken);
}

package com.example.nextune_backend.exception.error;

public class TrackNotFoundException extends RuntimeException {

    public TrackNotFoundException(String message) {
        super(message);
    }
}
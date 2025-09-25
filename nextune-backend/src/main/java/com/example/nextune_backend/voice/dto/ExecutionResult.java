package com.example.nextune_backend.voice.dto;

public record ExecutionResult(String speak, VoiceMessages.PlayerState state, String lang){}

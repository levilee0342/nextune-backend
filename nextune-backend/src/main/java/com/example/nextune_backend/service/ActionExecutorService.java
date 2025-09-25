package com.example.nextune_backend.service;

import com.example.nextune_backend.voice.dto.VoiceMessages;
import com.example.nextune_backend.voice.dto.ExecutionResult;


public interface ActionExecutorService {
    ExecutionResult execute(VoiceMessages.NluResp nlu);
}

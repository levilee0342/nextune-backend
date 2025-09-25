package com.example.nextune_backend.voice.dto;


import java.util.Map;

public class VoiceMessages {
    public static class ClientTranscript {
        public String type;
        public String text;
        public String locale;
        public String sessionId;
        public String device;
        public String userId;
    }

    public static class VoiceResult {
        public String type = "voice.result";
        public String intent;
        public Map<String, Object> entities;
        public String speak;
        public String lang;
        public PlayerState state;
    }

    public static class PlayerState {
        public String trackId;
        public Integer position_ms;
        public Boolean isPlaying;
        public Integer volume;
    }

    public static class NluResp {
        public String intent;
        public Map<String, Object> entities;
        public String answer;
        public String question;
        public String lang;
    }
}
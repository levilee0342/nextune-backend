package com.example.nextune_backend.voice.client;

import com.example.nextune_backend.voice.dto.VoiceMessages;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class NLUClient {
    private final WebClient nluClient;
    public NLUClient(WebClient nluClient) { this.nluClient = nluClient; }

    public VoiceMessages.NluResp parse(String text) {
        record NluReq(String text) {}
        return nluClient.post()
                .bodyValue(new NluReq(text))
                .retrieve()
                .bodyToMono(VoiceMessages.NluResp.class)
                .block();
    }
}
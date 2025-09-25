package com.example.nextune_backend.voice.handler;

import com.example.nextune_backend.voice.client.NLUClient;
import com.example.nextune_backend.voice.dto.VoiceMessages;
import com.example.nextune_backend.service.ActionExecutorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class VoiceSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper om = new ObjectMapper();
    private final NLUClient nlu;
    private final ActionExecutorService executor;

    public VoiceSocketHandler(NLUClient nlu, ActionExecutorService executor) {
        this.nlu = nlu; this.executor = executor;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        var auth = (Authentication) session.getAttributes().get("auth");

        if (auth != null) {
            var ctx = SecurityContextHolder.createEmptyContext();
            ctx.setAuthentication(auth);
            SecurityContextHolder.setContext(ctx);
        }

        var payload = message.getPayload();
        VoiceMessages.ClientTranscript req = om.readValue(payload, VoiceMessages.ClientTranscript.class);
        if (!"voice.transcript".equals(req.type)) return;

        // Fast-path ví dụ đơn giản:
        var t = req.text.toLowerCase();
        VoiceMessages.NluResp parsed;
        if (t.equals("pause") || t.contains("pause"))
            parsed = intent("pause");
        else if (t.equals("next") || t.contains("next"))
            parsed = intent("next");
        else
            parsed = nlu.parse(req.text); // gọi Python NLU

        var result = executor.execute(parsed);

        VoiceMessages.VoiceResult out = new VoiceMessages.VoiceResult();
        out.intent = parsed.intent;
        out.entities = parsed.entities;
        out.speak = result.speak();
        out.state = result.state();
        out.lang  = result.lang();
        session.sendMessage(new TextMessage(om.writeValueAsString(out)));
    }

    private VoiceMessages.NluResp intent(String name) {
        VoiceMessages.NluResp r = new VoiceMessages.NluResp();
        r.intent = name;
        r.entities = java.util.Map.of();
        return r;
    }
}
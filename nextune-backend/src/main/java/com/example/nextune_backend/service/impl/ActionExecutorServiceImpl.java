package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.voice.dto.VoiceMessages;
import com.example.nextune_backend.voice.dto.ExecutionResult;
import com.example.nextune_backend.service.ActionExecutorService;
import com.example.nextune_backend.service.PlayerService;
import com.example.nextune_backend.service.SearchService;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public class ActionExecutorServiceImpl implements ActionExecutorService {
    private final PlayerService player;
    private final SearchService search;

    public ActionExecutorServiceImpl(PlayerService player, SearchService search) {
        this.player = player;
        this.search = search;
    }

    @Override
    public ExecutionResult execute(VoiceMessages.NluResp nlu) {
        String intent = nlu.intent;
        String lang = nlu.lang != null ? nlu.lang : "en-US";
        Map<String,Object> e = nlu.entities == null ? Map.of() : nlu.entities;
        String speak;
        String trackId = "";
        int level = 0;

        try {
            switch (intent) {
                case "pause" -> { System.out.println("[Player] pause"); speak = vi("Paused.", "Đã tạm dừng.", lang); }
                case "play"  -> { System.out.println("[Player] play/resume");  speak = vi("Playing.", "Đang phát.", lang); }
                case "next"  -> { System.out.println("[Player] skip"); speak = vi("Skipping to next.", "Chuyển sang bài kế tiếp.", lang); }
                case "prev"  -> { System.out.println("[Player] go back"); speak = vi("Going back.", "Quay lại bài trước.", lang); }
                case "seek"  -> {
                    int offset = ((Number)e.getOrDefault("offset_ms", 0)).intValue();
                    boolean relative = (boolean)e.getOrDefault("relative", true);
                    player.seek(offset, relative);
                    speak = vi("Seeking.", "Đang tua.", lang);
                }
                case "set_volume" -> {
                    level = ((Number)e.getOrDefault("level", 50)).intValue();
                    level = Math.max(0, Math.min(100, level));
                    player.setVolume(level);
                    speak = vi("Volume set to ", "Đã đặt âm lượng ", lang) + level + "%.";
                }
                case "play_by_name" -> {
                    String title = (String)e.getOrDefault("title", "");
                    String artist = (String)e.getOrDefault("artist", null);
                    trackId = search.findTrackId(title, artist);
                    if (trackId == null) {
                        speak = "vi-VN".equals(lang)
                                ? "Mình chưa tìm thấy bài " + title + (artist!=null? " của " + artist : "") + ". Bạn nói rõ hơn nhé?"
                                : "I couldn't find the track " + title + (artist!=null? " by " + artist : "") + ". Could you be more specific?";
                        break;
                    }
                    player.playTrackId(trackId);
                    speak = "vi-VN".equals(lang)
                            ? ("Đang phát " + title + (artist!=null? " của " + artist : "") + ".")
                            : ("Playing " + title + (artist!=null? " by " + artist : "") + ".");
                }
                case "search" -> {
                    String q = String.valueOf(e.getOrDefault("q",
                            e.getOrDefault("query",
                                    e.getOrDefault("title", ""))));
                    System.out.println("[Player] search: " + q);
                    if ("vi-VN".equals(lang)) {
                        speak = q.isBlank() ? "Mình sẽ mở trang tìm kiếm."
                                : "Mình sẽ tìm \"" + q + "\" cho bạn.";
                    } else {
                        speak = q.isBlank() ? "I'll open the search page."
                                : "I'll search for \"" + q + "\".";
                    }
                }
                case "answer" -> {
                    speak = (nlu.answer != null && !nlu.answer.isBlank())
                            ? nlu.answer
                            : vi("Here’s the answer.", "Đây là câu trả lời.", lang);
                }
                case "clarify" -> {
                    speak = (nlu.question != null && !nlu.question.isBlank())
                            ? nlu.question
                            : vi("Could you clarify?", "Bạn có thể nói rõ hơn không?", lang);
                }
                default -> speak = vi("Sorry, I couldn't understand.", "Xin lỗi, mình chưa hiểu.", lang);
            }
        } catch (Exception ex) {
            speak = vi("Something went wrong executing the action.",
                    "Có lỗi xảy ra khi thực thi lệnh.", lang);
            ex.printStackTrace();
        }

        VoiceMessages.PlayerState state = new VoiceMessages.PlayerState();
        state.trackId = trackId;
        state.isPlaying = !"pause".equals(nlu.intent);
        state.position_ms = 0;
        state.volume = level;

        return new ExecutionResult(speak, state, lang);
    }

    private String vi(String en, String vi, String lang) {
        return "vi-VN".equals(lang) ? vi : en;
    }
}

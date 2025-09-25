package com.example.nextune_backend.utility;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

public class NormalizerUtility {
    private static final Set<String> STOP_EN = Set.of(
            "a","an","the","and","or","but","to","for","of","in","on","at","with","by","from","up","down",
            "my","your","yours" // thêm để xử lý truy vấn phổ biến
    );
    private static final Set<String> STOP_VI = Set.of(
            "một","cái","chiếc","và","hoặc","nhưng","của","cho","với","trong","trên","ở","bởi","từ","bài","bản"
    );

    public static String normalizeViEn(String s) {
        if (s == null) return "";
        String lower = s.toLowerCase().trim();
        String nfd = Normalizer.normalize(lower, Normalizer.Form.NFD);
        String noMarks = nfd.replaceAll("\\p{M}+", "");
        noMarks = noMarks.replace('đ', 'd');
        // bỏ ký tự không chữ/số
        noMarks = noMarks.replaceAll("[^a-z0-9\\s]+", " ").replaceAll("\\s+", " ").trim();
        return noMarks;
    }

    public static List<String> tokenize(String s) {
        String norm = normalizeViEn(s);
        if (norm.isBlank()) return List.of();
        String[] raw = norm.split("\\s+");
        List<String> out = new ArrayList<>(raw.length);
        for (String t : raw) {
            if (t.isBlank()) continue;
            if (STOP_EN.contains(t) || STOP_VI.contains(t)) continue;

            // stemming nhẹ: bỏ 's' cuối (yours -> your, smiles -> smile)
            if (t.length() > 3 && t.endsWith("s")) t = t.substring(0, t.length()-1);

            out.add(t);
        }
        return out;
    }

    /** Ghép token theo thứ tự chữ cái để dùng cho token-sort similarity */
    public static String joinSortedTokens(Collection<String> toks) {
        return toks.stream().sorted().collect(Collectors.joining(" "));
    }
}

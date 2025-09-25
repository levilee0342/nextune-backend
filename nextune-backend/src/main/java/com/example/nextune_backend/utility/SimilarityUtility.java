package com.example.nextune_backend.utility;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import java.util.*;

public class SimilarityUtility {
    private static final JaroWinklerSimilarity JW = new JaroWinklerSimilarity();

    public static double jaroWinkler(String a, String b) {
        if (a == null || b == null || a.isBlank() || b.isBlank()) return 0.0;
        return JW.apply(a, b);
    }

    /** Jaccard trên tập token */
    public static double tokenSetJaccard(Set<String> a, Set<String> b) {
        if (a.isEmpty() || b.isEmpty()) return 0.0;
        Set<String> inter = new HashSet<>(a);
        inter.retainAll(b);
        Set<String> uni = new HashSet<>(a);
        uni.addAll(b);
        return inter.size() / (double) uni.size(); // 0..1
    }

    /** Containment = |A∩B| / min(|A|,|B|) — đo độ “bao phủ” */
    public static double containment(Set<String> a, Set<String> b) {
        if (a.isEmpty() || b.isEmpty()) return 0.0;
        Set<String> inter = new HashSet<>(a);
        inter.retainAll(b);
        int min = Math.min(a.size(), b.size());
        return inter.size() / (double) min; // 0..1
    }
}

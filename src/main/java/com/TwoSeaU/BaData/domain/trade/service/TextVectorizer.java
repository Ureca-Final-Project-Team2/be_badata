package com.TwoSeaU.BaData.domain.trade.service;


import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class TextVectorizer {

    //수치화에 포함할 단어 모음집 (단어장)
    private final Map<String, Integer> vocabulary = new ConcurrentHashMap<>();

    private final AtomicInteger vocabularyIndex = new AtomicInteger(0);

    /**
     * TF-IDF 벡터 생성
     *
     * 키워드 수치화 메소드
     * vocabulary에 있으면 ++
     */
    public double[] createTfIdfVector(String text, List<String> corpus) {
        Map<String, Double> tf = calculateTF(text);
        Map<String, Double> idf = calculateIDF(corpus);

        double[] vector = new double[vocabulary.size()];

        for (Map.Entry<String, Double> entry : tf.entrySet()) {
            String term = entry.getKey();
            if (vocabulary.containsKey(term)) {
                int index = vocabulary.get(term);
                double tfValue = entry.getValue();
                double idfValue = idf.getOrDefault(term, 0.0);
                vector[index] = tfValue * idfValue;
            }
        }

        return vector;
    }

    private Map<String, Double> calculateTF(String text) {
        List<String> terms = tokenize(text);
        Map<String, Integer> termCounts = new HashMap<>();

        for (String term : terms) {
            termCounts.put(term, termCounts.getOrDefault(term, 0) + 1);
            // 어휘집에 추가
            vocabulary.putIfAbsent(term, vocabularyIndex.getAndIncrement());
        }

        Map<String, Double> tf = new HashMap<>();
        int totalTerms = terms.size();

        for (Map.Entry<String, Integer> entry : termCounts.entrySet()) {
            tf.put(entry.getKey(), (double) entry.getValue() / totalTerms);
        }

        return tf;
    }

    private Map<String, Double> calculateIDF(List<String> corpus) {
        Map<String, Integer> documentFrequency = new HashMap<>();

        for (String document : corpus) {
            Set<String> uniqueTerms = new HashSet<>(tokenize(document));
            for (String term : uniqueTerms) {
                documentFrequency.put(term, documentFrequency.getOrDefault(term, 0) + 1);
            }
        }

        Map<String, Double> idf = new HashMap<>();
        int totalDocuments = corpus.size();

        for (Map.Entry<String, Integer> entry : documentFrequency.entrySet()) {
            double idfValue = Math.log((double) totalDocuments / entry.getValue());
            idf.put(entry.getKey(), idfValue);
        }

        return idf;
    }

    private List<String> tokenize(String text) {
        // 간단한 토크나이저 (실제로는 한국어 형태소 분석기 사용 권장)
        return Arrays.stream(text.toLowerCase()
                        .replaceAll("[^가-힣a-z0-9\\s]", "")
                        .split("\\s+"))
                .filter(word -> word.length() > 1)
                .collect(Collectors.toList());
    }
}
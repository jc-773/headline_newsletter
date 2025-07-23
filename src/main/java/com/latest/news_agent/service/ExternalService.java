package com.latest.news_agent.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class ExternalService {

    private static final String URL = "https://api.openai.com/v1/chat/completions";
    private static final String QUERY = "generate a summary of top world news headlines based on recent events";
    private static final String PROMPT = "Provide a concise summary of today’s top world news headlines, with bullets focusing on major events in politics, economics, global conflicts, health, and technology. Exclude entertainment topics. Present the information in a clear and organized manner, covering the most impactful stories from various regions worldwide. If and only if applicable, include how any of the news items may be affecting the U.S. stock market. Include as much detail as possible while staying within a 1000-token limit.";

    WebClient client;

    public ExternalService() {
        client = WebClient.create();
    }

    public Mono<String> generateQueryForLatestWorldNews(String authorization) {
        return client.post().uri(URL).headers(headers -> {
             headers.setContentType(MediaType.APPLICATION_JSON);
             headers.setBearerAuth(authorization);
        })
        .bodyValue(buildRequest(QUERY))
        .retrieve()
        .bodyToMono(String.class);
    }

     private Map<String, Object> buildRequest(String query) {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of(
                "role", "assistant",
                "content", PROMPT));
        messages.add(Map.of(
                "role", "assistant",
                "content", query));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", messages);
        
        requestBody.put("max_tokens", 4000);

        return requestBody;
    } 
}

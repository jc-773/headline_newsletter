package com.latest.news_agent.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
public class ExternalService {
    private static Logger log = LoggerFactory.getLogger(ExternalService.class);

    private static final String URL = "https://api.openai.com/v1/chat/completions";
    private static final String QUERY = "you are an assistant that generates a summary of top world news headlines based on events that are most recent and relative to today's date";
    private static final String ECONOMICS_PROMPT = "Provide a concise summary of today’s (" + LocalDateTime.now() +  ") top world news headlines, focusing on major events in politics, economics, global conflicts, health, and technology. Exclude entertainment topics. Present the information in a clear and organized manner, covering the most impactful stories from various regions worldwide. If and only if applicable, include how any of the news items may be affecting the U.S. stock market. Include as much detail as possible while staying within a 1000-token limit.";
    private static final String GLOBAL_CONFLICTS_PROMPT = "Provide a concise summary of today’s top world news headlines, with bullets focusing on major events in politics, economics, global conflicts, health, and technology. Exclude entertainment topics. Present the information in a clear and organized manner, covering the most impactful stories from various regions worldwide. If and only if applicable, include how any of the news items may be affecting the U.S. stock market. Include as much detail as possible while staying within a 1000-token limit.";

    private static final String POLITICS_PROMPT = "What is the latest news with Israel, Iran, Yemen, Syria, and any other country worth mentioning.  Begin each topic with a bolded category title using this format: **Category Name**\\n. Follow with a single bullet point - summarizing the news story in 1–3 sentences. Separate each topic with two line breaks. Include as much detail as possible while staying within a 1000-token limit.\";";
    private static final String HEALTH_PROMPT = "What is the latest news with health around the world. Begin each topic with a bolded category title using this format: **Category Name**\\n. Follow with a single bullet point - summarizing the news story in 1–3 sentences. Separate each topic with two line breaks. Include as much detail as possible while staying within a 1000-token limit.";
    private static final String TECHNOLOGY_PROMPT = "What is the latest news with technology around the world. Begin each topic with a bolded category title using this format: **Category Name**\\n. Follow with a single bullet point - summarizing the news story in 1–3 sentences. Separate each topic with two line breaks. Include as much detail as possible while staying within a 1000-token limit.";

    WebClient client;

    public ExternalService() {
        client = WebClient.create();
    }

    public Mono<String> generateQueryForLatestWorldNews(String authorization, String subject) {
        return client.post().uri(URL).headers(headers -> {
             headers.setContentType(MediaType.APPLICATION_JSON);
             headers.setBearerAuth(authorization);
        })
        .bodyValue(buildRequest(subject))
        .retrieve()
        .bodyToMono(String.class);
    }

     private Map<String, Object> buildRequest(String subject) {
        List<Map<String, String>> messages = new ArrayList<>();
        String prompt = determineSubjectForPromptGeneration(subject);
        log.info("prompt generated: {}, \nfor the following subject: {}", prompt, subject);
        messages.add(Map.of("role", QUERY, "content", prompt));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");
        requestBody.put("messages", messages);
        
        requestBody.put("max_tokens", 4000);

        return requestBody;
    } 

    private String determineSubjectForPromptGeneration(String subject) {
        String prompt = "";
        switch (subject) {
            case "Technology":
                prompt = TECHNOLOGY_PROMPT;
                break;
            case "Politics":
                prompt = POLITICS_PROMPT;
                break;
            case "Health":
                prompt = HEALTH_PROMPT;
                break;
            default:
                break;
        }
        return prompt;
    }
}

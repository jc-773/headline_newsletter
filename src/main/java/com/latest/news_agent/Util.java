package com.latest.news_agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Util {

    private static Logger log = LoggerFactory.getLogger(Util.class);
    
     public static String mapResponse(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            System.out.println("response body: " + response);
            JsonNode root = mapper.readTree(response);
    
             JsonNode contentNode = root
            .path("choices")
            .path(0)
            .path("message")
            .path("content");

        return contentNode.asText().trim();
        } catch (Exception e) {
            log.error("oops... having trouble mapping the response", e);
        }
        return null;
    }
}

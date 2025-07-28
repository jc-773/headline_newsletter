package com.latest.news_agent.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.latest.news_agent.Util;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Component
public class CronServiceInit {

    private static final Logger log = LoggerFactory.getLogger(CronServiceInit.class);

    @Value("${openai.api.key:}")
    private String openAiKey;

    private final EmailService emailService;
    private final ExternalService externalService;
    private final ExecutorService executorService;

    @Autowired
    public CronServiceInit(ExternalService externalService, EmailService emailService, ExecutorService executorService) {
        this.externalService = externalService;
        this.emailService = emailService;
        this.executorService = executorService;
    }

    //@Scheduled(cron = "0 30 7 ? * MON-FRI", zone = "America/New_York") <- AWS lambda doesn't use @Scheduled
    public void startCronService() {
        log.info("cron service triggered...");
        var subjects = List.of("Politics", "Technology", "Health");
        Flux.fromIterable(subjects)
                .flatMap(subject -> externalService.generateQueryForLatestWorldNews(openAiKey, subject)
                        .map(response -> Map.entry(subject, Util.mapResponse(response)))
                        .subscribeOn(Schedulers.fromExecutor(executorService)))
                .collectList()
                .map(entries -> entries.stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> List.of(entry.getValue())
                        )))
                .doOnNext(f -> emailService.sendFormattedEmail(f))
                .block();
    }
}

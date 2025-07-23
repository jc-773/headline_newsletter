package com.latest.news_agent.service;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.latest.news_agent.Util;

import reactor.core.scheduler.Schedulers;


@Component
public class CronServiceInit {

    private static final Logger log = LoggerFactory.getLogger(CronServiceInit.class);

    @Value("${openai.api.key:}")
    private String openAiKey;

    private final EmailService emailService;
    private final ExternalService externalService;
    private final ExecutorService executorService;

    public CronServiceInit(ExternalService externalService, EmailService emailService, ExecutorService executorService) {
        this.externalService = externalService;
        this.emailService = emailService;
        this.executorService = executorService;
    }

    @Scheduled(cron = "0 30 7 ? * MON-FRI", zone = "America/New_York")
    public void startCronService() {
        log.info("cron service triggered...");
        externalService.generateQueryForLatestWorldNews(openAiKey)
            .map(Util::mapResponse)
            .doOnNext(f -> emailService.sendSimpleEmail(f))
            .subscribeOn(Schedulers.fromExecutor(executorService))
            .subscribe();
    }
}

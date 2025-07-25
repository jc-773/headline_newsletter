package com.latest.news_agent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.latest.news_agent.service.CronServiceInit;

@SpringBootApplication
@EnableScheduling
public class NewsAgentApplication {
	@Bean 
	public ExecutorService mainExecutorService() {
		return Executors.newVirtualThreadPerTaskExecutor();
	}
	public static void main(String[] args) {
		SpringApplication.run(NewsAgentApplication.class, args);
	}

	@Bean
    public CommandLineRunner runJob(CronServiceInit cronServiceInit) {
        return args -> {
            cronServiceInit.startCronService();
			System.exit(0);
        };
    }
}

package com.latest.news_agent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.latest.news_agent.service.CronServiceInit;

import jakarta.annotation.PostConstruct;

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

	@PostConstruct
	public void dumpEnv() {
		System.out.println("=== ENV DUMP ===");
		System.getenv().forEach((k, v) -> {
			if (k.toLowerCase().contains("mail")) {
				System.out.println(k + " = " + v);
			}
		});
		System.out.println("================");
	}
}

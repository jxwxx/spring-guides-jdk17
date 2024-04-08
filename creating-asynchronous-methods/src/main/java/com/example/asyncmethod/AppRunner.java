package com.example.asyncmethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class AppRunner implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);
    private final GitHubLookupService githubLookupService;

    public AppRunner(GitHubLookupService githubLookupService) {
        this.githubLookupService = githubLookupService;
    }

    @Override
    public void run(String... args) throws Exception {
        long start = System.currentTimeMillis();

        String[] users = {"PivotalSoftware", "CloudFoundry", "Spring-Projects"};
        List<CompletableFuture<User>> pages = new ArrayList<>();

        for (String user : users) {
            pages.add(githubLookupService.findUser(user));
        }

        CompletableFuture<Void> allPages = CompletableFuture.allOf(pages.toArray(new CompletableFuture[0]));
        allPages.join();

        logger.info("Elapsed time: {}", System.currentTimeMillis() - start);

        for (CompletableFuture<User> page : pages) {
            logger.info("--> {}", page.get());
        }
    }
}

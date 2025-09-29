package com.ragchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.ragchat")
@EntityScan("com.ragchat.model")
@EnableJpaRepositories("com.ragchat.repo")
public class RagChatStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(RagChatStoreApplication.class, args);
    }

}

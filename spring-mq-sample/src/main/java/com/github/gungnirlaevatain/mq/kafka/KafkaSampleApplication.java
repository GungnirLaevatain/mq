package com.github.gungnirlaevatain.mq.kafka;

import com.github.gungnirlaevatain.mq.EnableMq;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableMq
@SpringBootApplication(scanBasePackages = "com.github.gungnirlaevatain.mq.kafka")
public class KafkaSampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(KafkaSampleApplication.class);
    }
}

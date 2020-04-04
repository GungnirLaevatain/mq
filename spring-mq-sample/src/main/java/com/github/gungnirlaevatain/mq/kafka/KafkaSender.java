package com.github.gungnirlaevatain.mq.kafka;

import com.github.gungnirlaevatain.mq.producer.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.UUID;

@Slf4j
@Component
public class KafkaSender {
    @Autowired
    private Producer producer;
    private volatile boolean flag = true;

    @PostConstruct
    public void init() {
        new Thread(() -> {
            while (flag) {
                String uuid = UUID.randomUUID().toString();
                producer.send("test", uuid);
                log.info("send test message [{}]", uuid);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @PreDestroy
    public void stop() {
        flag = false;
    }
}

package com.github.gungnirlaevatain.mq.kafka;

import com.github.gungnirlaevatain.mq.consumer.MessageRecord;
import com.github.gungnirlaevatain.mq.consumer.MqListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class KafkaHandler {

    @MqListener(groupName = "test-consumer", topics = "test")
    public void handler(MessageRecord messageRecord) {
        log.info("received test message [{}]", new String(messageRecord.getBody(), StandardCharsets.UTF_8));
    }
}

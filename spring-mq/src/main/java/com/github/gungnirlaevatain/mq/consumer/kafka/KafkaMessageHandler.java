package com.github.gungnirlaevatain.mq.consumer.kafka;

import com.github.gungnirlaevatain.mq.consumer.AbstractMqMessageHandler;
import com.github.gungnirlaevatain.mq.consumer.MessageRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
public class KafkaMessageHandler extends AbstractMqMessageHandler<ConsumerRecord<?, ?>> {

    public KafkaMessageHandler(Object bean, Method method) {
        super(bean, method);
    }

    @KafkaListener
    public void listen(ConsumerRecord<?, ?> record) throws InvocationTargetException, IllegalAccessException {
        handleMessage(record);
    }

    @Override
    protected MessageRecord getMessageRecord(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            return new MessageRecord(record.topic(), record.key(),
                    null, record.value().toString().getBytes(StandardCharsets.UTF_8), record.timestamp(), 0);
        }
        return null;
    }

    @Override
    protected Object getMessageBody(ConsumerRecord<?, ?> record) {
        return record.value();
    }
}



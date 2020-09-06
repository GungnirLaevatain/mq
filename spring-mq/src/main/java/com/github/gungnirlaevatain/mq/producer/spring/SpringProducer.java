package com.github.gungnirlaevatain.mq.producer.spring;

import com.github.gungnirlaevatain.mq.MqException;
import com.github.gungnirlaevatain.mq.producer.MessageSendResult;
import com.github.gungnirlaevatain.mq.producer.MqResultCallback;
import com.github.gungnirlaevatain.mq.producer.Producer;
import com.github.gungnirlaevatain.mq.producer.ProducerProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

public class SpringProducer implements Producer {
    @Autowired
    private ApplicationEventPublisher publisher;

    @Override
    public void start() {

    }

    @Override
    public void init(ProducerProperty producerProperty) {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public MessageSendResult send(String topic, String tags, String keys, Object msg, int delay) throws MqException {
        return null;
    }

    @Override
    public void asyncSend(String topic, String tags, String keys, Object msg, int delay, MqResultCallback callback) throws MqException {

    }
}

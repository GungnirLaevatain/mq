package com.github.gungnirlaevatain.mq.producer.mock;

import com.github.gungnirlaevatain.mq.MqException;
import com.github.gungnirlaevatain.mq.producer.AbstractProducer;
import com.github.gungnirlaevatain.mq.producer.MessageSendResult;
import com.github.gungnirlaevatain.mq.producer.MqResultCallback;

public class MockProducer extends AbstractProducer {
    @Override
    public MessageSendResult send(String topic, String tags, String keys, Object msg) throws MqException {
        return send(topic, tags, keys, msg, 0);
    }

    @Override
    public MessageSendResult send(String topic, String tags, String keys, Object msg, int delay) throws MqException {
        return new MessageSendResult().setSuccess(true);
    }

    @Override
    public void asyncSend(String topic, String tags, String keys, Object msg, MqResultCallback callback) throws MqException {
        asyncSend(topic, tags, keys, msg, 0, callback);
    }

    @Override
    public void asyncSend(String topic, String tags, String keys, Object msg, int delay, MqResultCallback callback) throws MqException {
        callback.success(new MessageSendResult().setSuccess(true));
    }
}

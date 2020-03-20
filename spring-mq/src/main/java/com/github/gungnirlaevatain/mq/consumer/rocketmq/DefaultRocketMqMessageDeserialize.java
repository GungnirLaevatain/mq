package com.github.gungnirlaevatain.mq.consumer.rocketmq;

import com.github.gungnirlaevatain.mq.MqException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

public class DefaultRocketMqMessageDeserialize implements RocketMqMessageDeserialize {
    @Override
    public Object deserialize(byte[] bytes, Map<String, String> prop) {
        try (ByteArrayInputStream stream = new ByteArrayInputStream(bytes); ObjectInputStream objectStream = new ObjectInputStream(stream)) {
            return objectStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new MqException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Map<String, String> prop, Class<T> clazz) {
        return (T) deserialize(bytes, prop);
    }
}

package com.github.gungnirlaevatain.mq.producer.rocketmq;

import com.github.gungnirlaevatain.mq.MqException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

public class DefaultRocketMqMessageSerialize implements RocketMqMessageSerialize {
    @Override
    public byte[] serialize(Object obj, Map<String, String> prop) {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream(); ObjectOutputStream objectStream = new ObjectOutputStream(stream)) {
            objectStream.writeObject(obj);
            return stream.toByteArray();
        } catch (IOException e) {
            throw new MqException(e);
        }
    }
}

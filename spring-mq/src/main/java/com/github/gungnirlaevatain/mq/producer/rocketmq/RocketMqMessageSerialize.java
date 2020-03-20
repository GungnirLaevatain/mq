package com.github.gungnirlaevatain.mq.producer.rocketmq;

import java.util.Map;

public interface RocketMqMessageSerialize {

    byte[] serialize(Object obj, Map<String, String> prop);

}

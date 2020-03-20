

package com.github.gungnirlaevatain.mq.consumer.rocketmq;

import com.alibaba.rocketmq.client.consumer.MQPushConsumer;


public interface CustomRocketMqConsumer extends MQPushConsumer {
    /**
     * Init.
     * 初始化
     *
     * @param consumerProperty the consumer property
     */
    void init(RocketMqConsumerProperty consumerProperty);
}

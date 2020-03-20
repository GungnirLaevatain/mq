

package com.github.gungnirlaevatain.mq.consumer.rocketmq;

import com.alibaba.rocketmq.client.ClientConfig;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
import com.github.gungnirlaevatain.mq.consumer.ConsumerProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = RocketMqConsumerProperty.PREFIX)
@Data
public class RocketMqConsumerProperty {
    public static final String PREFIX = ConsumerProperty.PREFIX + ".rocketmq";

    private String consumerGroup;
    private int maxConsumer = 64;
    private int minConsumer = 20;
    private ConsumeFromWhere consumeFromWhere = ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET;
    private long adjustThreadPoolNumsThreshold = 100000;
    private int consumeConcurrentlyMaxSpan = 2000;
    private int pullThresholdForQueue = 1000;
    private long pullInterval = 0;
    private int consumeMessageBatchMaxSize = 1;
    private int pullBatchSize = 32;
    private boolean postSubscriptionWhenPull = false;
    private boolean unitMode = false;
    private boolean order = false;
    private MessageModel messageModel = MessageModel.CLUSTERING;

    private Class<? extends CustomRocketMqConsumer> customConsumer;
    private Class<? extends RocketMqMessageDeserialize> messageDeserialize;

    @NestedConfigurationProperty
    private ClientConfig clientConfig;

}

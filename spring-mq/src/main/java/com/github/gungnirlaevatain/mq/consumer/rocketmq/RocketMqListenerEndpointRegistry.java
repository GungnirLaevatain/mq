package com.github.gungnirlaevatain.mq.consumer.rocketmq;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.MQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerOrderly;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
import com.github.gungnirlaevatain.mq.MqException;
import com.github.gungnirlaevatain.mq.consumer.MqListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Getter
public class RocketMqListenerEndpointRegistry implements DisposableBean, SmartLifecycle {
    protected static final int DEFAULT_PHASE = Integer.MAX_VALUE - 100;

    private Map<String, MQPushConsumer> mqConsumerMap = new HashMap<>(16);
    private Map<String, RocketMqMessageDispatch> dispatchMap = new HashMap<>(16);
    private volatile boolean isRunning;

    @Autowired
    private Environment environment;

    @Autowired
    private RocketMqConsumerProperty consumerProperty;

    public void addConsumer(MqListener listener, RocketMqMessageHandler handler) {
        addMQPushConsumer(listener, handler);
    }

    @Override
    public void destroy() {
        stop();
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public void start() {
        isRunning = true;
        for (Map.Entry<String, MQPushConsumer> entry : mqConsumerMap.entrySet()) {
            try {
                entry.getValue().start();
                log.info("RocketMQ consumer [{}] start", entry.getKey());
            } catch (MQClientException e) {
                stop();
                throw new MqException(e);
            }
        }
    }

    @Override
    public void stop() {
        isRunning = false;
        for (Map.Entry<String, MQPushConsumer> entry : mqConsumerMap.entrySet()) {
            try {
                entry.getValue().shutdown();
                log.info("RocketMQ consumer [{}] stop", entry.getKey());
            } catch (Exception e) {
                log.error("RocketMQ consumer [{}] stop failed", entry.getKey());
            }
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }


    private void addMQPushConsumer(MqListener listener, RocketMqMessageHandler handler) {

        String groupName = listener.groupName();
        if (StringUtils.isEmpty(groupName)) {
            groupName = consumerProperty.getConsumerGroup();
        }
        groupName = environment.resolvePlaceholders(groupName);
        if (listener.topics().length > 0) {
            String tag = createTags(listener.tags());
            RocketMqMessageDispatch dispatch = dispatchMap.getOrDefault(groupName, new RocketMqMessageDispatch(groupName));
            MQPushConsumer consumer = mqConsumerMap.getOrDefault(groupName, createConsumer(listener, groupName));
            if (!consumerProperty.isOrder()) {
                log.info("RocketMq mode now is Concurrently");
                consumer.registerMessageListener((MessageListenerConcurrently) dispatch::dispatch);
            } else {
                log.info("RocketMq mode now is Orderly");
                consumer.registerMessageListener((MessageListenerOrderly) dispatch::dispatch);
            }
            for (String topic : listener.topics()) {
                topic = environment.resolvePlaceholders(topic);
                try {
                    dispatch.registerHandler(topic, tag, handler);
                    Set<String> tagSet = dispatch.getTagByTopic(topic);
                    String finalTag = createTags(tagSet.toArray(new String[0]));
                    consumer.subscribe(topic, finalTag);
                    log.info("MQConsumerList add topic : [{}], tag : [{}] succeed", topic, tag);
                } catch (MQClientException e) {
                    log.error("MQConsumerList add topic : [{}], tag : [{}] failed", topic, tag);
                }
            }
            dispatchMap.putIfAbsent(groupName, dispatch);
            mqConsumerMap.putIfAbsent(groupName, consumer);
        }

    }

    private MQPushConsumer createConsumer(MqListener listener, String groupName) {
        if (consumerProperty.getCustomConsumer() != null) {
            try {
                CustomRocketMqConsumer consumer = consumerProperty.getCustomConsumer().newInstance();
                consumer.init(consumerProperty);
                return consumer;
            } catch (InstantiationException | IllegalAccessException e) {
                throw new MqException(e);
            }
        } else {
            DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer();
            defaultMQPushConsumer.setConsumerGroup(groupName);
            defaultMQPushConsumer.resetClientConfig(consumerProperty.getClientConfig());
            defaultMQPushConsumer.setConsumeThreadMax(listener.maxConsumer() == 0 ? consumerProperty.getMaxConsumer() : listener.maxConsumer());
            defaultMQPushConsumer.setConsumeThreadMin(listener.minConsumer() == 0 ? consumerProperty.getMinConsumer() : listener.minConsumer());
            defaultMQPushConsumer.setConsumeFromWhere(consumerProperty.getConsumeFromWhere());
            defaultMQPushConsumer.setUnitMode(consumerProperty.isUnitMode());
            defaultMQPushConsumer.setAdjustThreadPoolNumsThreshold(consumerProperty.getAdjustThreadPoolNumsThreshold());
            defaultMQPushConsumer.setConsumeConcurrentlyMaxSpan(consumerProperty.getConsumeConcurrentlyMaxSpan());
            defaultMQPushConsumer.setConsumeMessageBatchMaxSize(consumerProperty.getConsumeMessageBatchMaxSize());
            defaultMQPushConsumer.setPullThresholdForQueue(consumerProperty.getPullThresholdForQueue());
            defaultMQPushConsumer.setPullInterval(consumerProperty.getPullInterval());
            defaultMQPushConsumer.setPullBatchSize(consumerProperty.getPullBatchSize());
            defaultMQPushConsumer.setPostSubscriptionWhenPull(consumerProperty.isPostSubscriptionWhenPull());
            switch (listener.type()) {
                case CLUSTERING:
                    defaultMQPushConsumer.setMessageModel(MessageModel.CLUSTERING);
                    break;
                case BROADCASTING:
                    defaultMQPushConsumer.setMessageModel(MessageModel.BROADCASTING);
                    break;
                default: {
                    defaultMQPushConsumer.setMessageModel(consumerProperty.getMessageModel());
                }
            }
            return defaultMQPushConsumer;
        }
    }

    private String createTags(String[] tags) {
        StringBuilder sb = new StringBuilder();
        if (tags != null && tags.length > 0) {
            for (int i = 0; i < tags.length - 1; i++) {
                if ("*".equals(tags[i])) {
                    return "*";
                }
                sb.append(environment.resolvePlaceholders(tags[i])).append("||");
            }
            sb.append(tags[tags.length - 1]);
        }
        return sb.length() > 0 ? sb.toString() : "*";
    }
}

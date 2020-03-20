
package com.github.gungnirlaevatain.mq.producer.rocketmq;

import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.*;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.github.gungnirlaevatain.mq.MqException;
import com.github.gungnirlaevatain.mq.producer.AbstractProducer;
import com.github.gungnirlaevatain.mq.producer.MessageSendResult;
import com.github.gungnirlaevatain.mq.producer.MqResultCallback;
import com.github.gungnirlaevatain.mq.producer.ProducerProperty;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RocketMqProducer extends AbstractProducer {

    private MQProducer mqProducer;

    private RocketMqMessageSerialize rocketMqMessageSerialize = new DefaultRocketMqMessageSerialize();

    @Override
    public void start() {
        try {
            mqProducer.start();
            log.info("RocketMQ producer has been start");
        } catch (MQClientException e) {
            throw new MqException(e);
        }
    }

    @Override
    public void init(ProducerProperty producerProperty) {
        RocketMqProducerProperty rocketMqProducerProperty = (RocketMqProducerProperty) producerProperty;
        mqProducer = createDefaultMQProducer(rocketMqProducerProperty);
        if (rocketMqProducerProperty.getMessageSerialize() != null) {
            try {
                rocketMqMessageSerialize = rocketMqProducerProperty.getMessageSerialize().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
        log.debug("rocketMQ producer has been init");
    }

    @Override
    public void shutdown() {
        mqProducer.shutdown();
        log.info("RocketMQ producer has been shutdown");
    }

    @Override
    public MessageSendResult send(String topic, String tags, String keys, Object msg) throws MqException {
        return send(topic, tags, keys, msg, 0);
    }

    @Override
    public MessageSendResult send(String topic, String tags, String keys, Object msg, int delay) throws MqException {
        return send(createMessage(topic, tags, keys, msg, delay));
    }

    @Override
    public void asyncSend(String topic, String tags, String keys, Object msg, MqResultCallback callback) throws MqException {
        asyncSend(topic, tags, keys, msg, 0, callback);
    }

    @Override
    public void asyncSend(String topic, String tags, String keys, Object msg, int delay, MqResultCallback callback) throws MqException {
        asyncSend(createMessage(topic, tags, keys, msg, delay), callback);
    }

    private RocketMqSendResult send(Message message) {
        try {
            SendResult sendResult = mqProducer.send(message);
            return createResult(sendResult);
        } catch (MQClientException | RemotingException | MQBrokerException | InterruptedException e) {
            throw new MqException(e);
        }
    }

    private void asyncSend(Message message, MqResultCallback callback) {
        try {
            mqProducer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    callback.success(createResult(sendResult));
                }

                @Override
                public void onException(Throwable throwable) {
                    callback.fail(throwable);
                }
            });
        } catch (MQClientException | RemotingException | InterruptedException e) {
            throw new MqException(e);
        }
    }

    private Message createMessage(String topic, String tags, String keys, Object msg, int delay) {
        Message message = new Message();
        message.setKeys(keys);
        message.setTags(tags);
        message.setTopic(topic);
        message.setDelayTimeLevel(delay);
        Map<String, String> properties = new HashMap<>(1);
        message.setBody(rocketMqMessageSerialize.serialize(msg, properties));
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            message.putUserProperty(entry.getKey(), entry.getValue());
        }
        return message;
    }

    private RocketMqSendResult createResult(SendResult sendResult) {
        RocketMqSendResult rocketMqResult = RocketMqSendResult.builder()
                .sendStatus(sendResult.getSendStatus())
                .messageQueue(sendResult.getMessageQueue())
                .queueOffset(sendResult.getQueueOffset())
                .transactionId(sendResult.getTransactionId())
                .msgId(sendResult.getMsgId())
                .build();
        if (sendResult.getSendStatus().equals(SendStatus.SEND_OK)) {
            rocketMqResult.setSuccess(true);
        } else {
            rocketMqResult.setSuccess(false);
        }
        return rocketMqResult;
    }

    private TransactionMQProducer createDefaultMQProducer(RocketMqProducerProperty producerProperty) {
        TransactionMQProducer transactionMQProducer = new TransactionMQProducer();
        transactionMQProducer.resetClientConfig(producerProperty.getClientConfig());
        transactionMQProducer.setRetryTimesWhenSendFailed(producerProperty.getRetryTimesWhenSendFailed());
        transactionMQProducer.setRetryAnotherBrokerWhenNotStoreOK(producerProperty.isRetryAnotherBrokerWhenNotStoreOK());
        transactionMQProducer.setProducerGroup(producerProperty.getProducerGroup());
        transactionMQProducer.setMaxMessageSize(producerProperty.getMaxMessageSize());
        transactionMQProducer.setSendMsgTimeout(producerProperty.getSendMsgTimeout());
        transactionMQProducer.setCompressMsgBodyOverHowmuch(producerProperty.getCompressMsgBodyOverHowmuch());
        return transactionMQProducer;
    }
}

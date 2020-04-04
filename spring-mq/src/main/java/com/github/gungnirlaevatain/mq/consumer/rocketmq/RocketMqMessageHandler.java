package com.github.gungnirlaevatain.mq.consumer.rocketmq;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.github.gungnirlaevatain.mq.consumer.AbstractMqMessageHandler;
import com.github.gungnirlaevatain.mq.consumer.MessageRecord;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;


@Slf4j
public class RocketMqMessageHandler extends AbstractMqMessageHandler<MessageExt> {

    private RocketMqMessageDeserialize messageDeserialize;

    public RocketMqMessageHandler(Object bean, Method method, RocketMqMessageDeserialize messageDeserialize) {
        super(bean, method);
        this.messageDeserialize = messageDeserialize;
    }

    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        log.debug("invoke method received record is {}", list);
        try {
            sendMessage(list);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            log.error("consumeMessage fail, because of ", e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }

    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list, ConsumeOrderlyContext consumeOrderlyContext) {
        log.debug("invoke method received record is {}", list);
        try {
            sendMessage(list);
            return ConsumeOrderlyStatus.SUCCESS;
        } catch (Exception e) {
            log.error("consumeMessage fail, because of ", e);
            return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
        }
    }

    private void sendMessage(List<MessageExt> list) throws InvocationTargetException, IllegalAccessException {
        for (MessageExt messageExt : list) {
            handleMessage(messageExt);
        }
    }


    @Override
    protected MessageRecord getMessageRecord(MessageExt messageExt) {
        return new MessageRecord(messageExt.getTopic(), messageExt.getKeys(),
                messageExt.getTags(), messageExt.getBody(),
                messageExt.getStoreTimestamp(), messageExt.getDelayTimeLevel());
    }

    @Override
    protected Object getMessageBody(MessageExt msg) {
        return messageDeserialize.deserialize(msg.getBody(), msg.getProperties());
    }

}



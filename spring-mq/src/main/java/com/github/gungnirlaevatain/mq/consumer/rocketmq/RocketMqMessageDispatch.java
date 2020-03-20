
package com.github.gungnirlaevatain.mq.consumer.rocketmq;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import com.alibaba.rocketmq.common.message.MessageExt;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Data
public class RocketMqMessageDispatch {

    private static final String ALL_TAGS = "*";

    private Map<String, Map<String, RocketMqMessageHandler>> topicDispatchMap = new HashMap<>();
    private String groupName;

    public RocketMqMessageDispatch(String groupName) {
        this.groupName = groupName;
    }

    public void registerHandler(String topic, String tag, RocketMqMessageHandler handler) {
        if (StringUtils.isEmpty(tag)) {
            tag = ALL_TAGS;
        }
        Map<String, RocketMqMessageHandler> handlerMap = topicDispatchMap.computeIfAbsent(topic,
                v -> new HashMap<>(16));
        handlerMap.put(tag, handler);
        log.info("group [{}] register topic [{}] tag [{}] by handler [{}]", groupName, topic, tag, handler);
    }


    public ConsumeConcurrentlyStatus dispatch(List<MessageExt> messageExts,
                                              ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        Map<String, List<MessageExt>> messageExtTopicGroup = messageExts.stream()
                .collect(Collectors.groupingBy(MessageExt::getTopic));

        for (Map.Entry<String, List<MessageExt>> entry : messageExtTopicGroup.entrySet()) {
            Map<String, RocketMqMessageHandler> tagDispatchMap = topicDispatchMap.get(entry.getKey());
            if (tagDispatchMap == null) {
                log.error("group [{}] cannot find handler for topic [{}]", groupName, entry.getKey());
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }

            Map<String, List<MessageExt>> messageExtTagGroup =
                    entry.getValue().stream().collect(Collectors.groupingBy(MessageExt::getTags));
            for (Map.Entry<String, List<MessageExt>> tagEntry : messageExtTagGroup.entrySet()) {
                RocketMqMessageHandler rocketMqMessageHandler = tagDispatchMap.get(tagEntry.getKey());
                if (rocketMqMessageHandler == null) {
                    rocketMqMessageHandler = tagDispatchMap.get(ALL_TAGS);
                    if (rocketMqMessageHandler == null) {
                        log.error("group [{}] cannot find handler for topic [{}], tag [{}]", groupName,
                                entry.getKey(), tagEntry.getKey());
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                }
                ConsumeConcurrentlyStatus result = rocketMqMessageHandler.consumeMessage(entry.getValue(),
                        consumeConcurrentlyContext);
                if (result != ConsumeConcurrentlyStatus.CONSUME_SUCCESS) {
                    return result;
                }
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    public ConsumeOrderlyStatus dispatch(List<MessageExt> messageExts, ConsumeOrderlyContext consumeOrderlyContext) {
        Map<String, List<MessageExt>> messageExtTopicGroup =
                messageExts.stream().collect(Collectors.groupingBy(MessageExt::getTopic));

        for (Map.Entry<String, List<MessageExt>> entry : messageExtTopicGroup.entrySet()) {
            Map<String, RocketMqMessageHandler> tagDispatchMap = topicDispatchMap.get(entry.getKey());
            if (tagDispatchMap == null) {
                log.error("group [{}] cannot find handler for topic [{}]", groupName, entry.getKey());
                return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
            }

            Map<String, List<MessageExt>> messageExtTagGroup =
                    entry.getValue().stream().collect(Collectors.groupingBy(MessageExt::getTags));
            for (Map.Entry<String, List<MessageExt>> tagEntry : messageExtTagGroup.entrySet()) {
                RocketMqMessageHandler rocketMqMessageHandler = tagDispatchMap.get(tagEntry.getKey());
                if (rocketMqMessageHandler == null) {
                    rocketMqMessageHandler = tagDispatchMap.get(ALL_TAGS);
                    if (rocketMqMessageHandler == null) {
                        log.error("group [{}] cannot find handler for topic [{}], tag [{}]", groupName,
                                entry.getKey(), tagEntry.getKey());
                        return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                    }
                }
                ConsumeOrderlyStatus result = rocketMqMessageHandler.consumeMessage(entry.getValue(),
                        consumeOrderlyContext);
                if (result != ConsumeOrderlyStatus.SUCCESS) {
                    return result;
                }
            }
        }
        return ConsumeOrderlyStatus.SUCCESS;
    }

    public Set<String> getTagByTopic(String topic) {
        Map<String, RocketMqMessageHandler> handlerMap = topicDispatchMap.get(topic);
        if (handlerMap == null || handlerMap.isEmpty()) {
            return Collections.emptySet();
        }
        return handlerMap.keySet();
    }

}

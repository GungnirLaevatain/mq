
package com.github.gungnirlaevatain.mq.producer.rocketmq;

import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.github.gungnirlaevatain.mq.producer.MessageSendResult;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class RocketMqSendResult extends MessageSendResult {

    private SendStatus sendStatus;
    private MessageQueue messageQueue;
    private long queueOffset;
    private String transactionId;
    private String msgId;

}

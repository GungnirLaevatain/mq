
package com.github.gungnirlaevatain.mq.producer.rocketmq;

import com.alibaba.rocketmq.client.ClientConfig;
import com.github.gungnirlaevatain.mq.MqType;
import com.github.gungnirlaevatain.mq.producer.ProducerProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = RocketMqProducerProperty.PREFIX)
@Data
public class RocketMqProducerProperty extends ProducerProperty {
    public static final String PREFIX = ProducerProperty.PREFIX + ".rocketmq";

    public RocketMqProducerProperty() {
        super();
        super.setType(MqType.RocketMQ);
    }

    /**
     * The ProducerProperty group.
     * 发送同一类消息的设置为同一个group，保证唯一,默认不需要设置，rocketmq会使用ip@pid(pid代表jvm名字)作为唯一标示
     */
    private String producerGroup;
    /**
     * The Send msg timeout.
     * 发送消息超时时间
     */
    private int sendMsgTimeout = 3000;
    /**
     * The Compress msg body over howmuch.
     * 消息Body超过多大开始压缩（Consumer收到消息会自动解压缩）,单位字节，默认4k字节
     */
    private int compressMsgBodyOverHowmuch = 1024 * 4;
    /**
     * The Retry times when send failed.
     * 当同步方式发送失败时重试次数
     */
    private int retryTimesWhenSendFailed = 2;
    /**
     * The Retry times when send async failed.
     * 当异步方式发送失败时重试次数
     */
    private int retryTimesWhenSendAsyncFailed = 2;

    /**
     * The Retry another broker when not store ok.
     * 如果发送消息返回sendResult，但是sendStatus!=SEND_OK,是否重试发送
     */
    private boolean retryAnotherBrokerWhenNotStoreOK = false;
    /**
     * The Max message size.
     * 消息最大长度,默认4m
     */
    private int maxMessageSize = 1024 * 1024 * 4;

    /**
     * 数据序列化接口
     */
    private Class<? extends RocketMqMessageSerialize> messageSerialize;

    /**
     * The Client config.
     */
    @NestedConfigurationProperty
    private ClientConfig clientConfig;
}

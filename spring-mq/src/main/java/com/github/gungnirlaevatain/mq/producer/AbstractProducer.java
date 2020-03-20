
package com.github.gungnirlaevatain.mq.producer;

import com.github.gungnirlaevatain.mq.MqException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractProducer implements Producer {
    /**
     * Start.
     * 启动
     */
    @Override
    public void start() {
        log.info("MQ Producer has been started");
    }

    /**
     * Start.
     * 启动
     *
     * @param producerProperty the producer property
     *                         配置文件
     */
    @Override
    public void init(ProducerProperty producerProperty) {
        log.info("MQ Producer has been init");
    }

    /**
     * Shutdown.
     * 关闭
     */
    @Override
    public void shutdown() {
        log.info("MQ Producer has been shut down");
    }

    /**
     * Send.
     * 发送消息
     *
     * @param topic the topic/the exchange
     *              话题/交换器
     * @param msg   the msg
     *              消息内容
     * @return the mq result
     * @throws MqException the mq exception
     */
    @Override
    public MessageSendResult send(String topic, Object msg) throws MqException {
        return send(topic, null, msg);
    }

    /**
     * Send.
     * 发送消息
     *
     * @param topic the topic/the exchange
     *              话题/交换器
     * @param tags  the tags/the routeKey
     *              标签(有些mq不支持,例如kafka)
     * @param msg   the msg
     *              消息内容
     * @return the mq result
     * @throws MqException the mq exception
     */
    @Override
    public MessageSendResult send(String topic, String tags, Object msg) throws MqException {
        return send(topic, tags, null, msg);
    }

    /**
     * Send.
     * 发送消息
     *
     * @param topic the topic/the exchange
     *              话题
     * @param tags  the tags
     *              标签(有些mq不支持,例如kafka)
     * @param keys  the keys/the routeKey
     *              关键词(有些mq不支持)
     * @param msg   the msg
     *              消息内容
     * @return the mq result
     * @throws MqException the mq exception
     */
    @Override
    public MessageSendResult send(String topic, String tags, String keys, Object msg) throws MqException {
        throw new MqException("This is abstract producer so that can not send message");
    }

    /**
     * Send.
     * 发送消息
     *
     * @param topic the topic
     *              话题
     * @param tags  the tags
     *              标签(有些mq不支持,例如kafka)
     * @param keys  the keys
     *              关键词(有些mq不支持)
     * @param msg   the msg
     *              消息内容
     * @param delay the delay
     *              延迟等级(有些mq不支持,例如kafka)
     * @return the mq result
     * @throws MqException the mq exception
     */
    @Override
    public MessageSendResult send(String topic, String tags, String keys, Object msg, int delay) throws MqException {
        throw new MqException("This is abstract producer so that can not send message");
    }

    /**
     * Async Send.
     * 发送消息
     *
     * @param topic    the topic
     *                 话题
     * @param msg      the msg
     *                 消息内容
     * @param callback the callback
     *                 回调接口
     * @throws MqException the mq exception
     */
    @Override
    public void asyncSend(String topic, Object msg, MqResultCallback callback) throws MqException {
         asyncSend(topic,null, msg, callback);
    }

    /**
     * Async Send.
     * 发送消息
     *
     * @param topic    the topic
     *                 话题
     * @param tags     the tags
     *                 标签(有些mq不支持,例如kafka)
     * @param msg      the msg
     *                 消息内容
     * @param callback the callback
     *                 回调接口
     * @throws MqException the mq exception
     */
    @Override
    public void asyncSend(String topic, String tags, Object msg, MqResultCallback callback) throws MqException {
         asyncSend(topic, tags,null, msg, callback);
    }

    /**
     * Async Send.
     * 发送消息
     *
     * @param topic    the topic
     *                 话题
     * @param tags     the tags
     *                 标签(有些mq不支持,例如kafka)
     * @param keys     the keys
     *                 关键词(有些mq不支持)
     * @param msg      the msg
     *                 消息内容
     * @param callback the callback
     *                 回调接口
     * @throws MqException the mq exception
     */
    @Override
    public void asyncSend(String topic, String tags, String keys, Object msg, MqResultCallback callback) throws MqException {
        throw new MqException("This is abstract producer so that can not send message");
    }

    /**
     * Async Send.
     * 发送消息
     *
     * @param topic    the topic
     *                 话题
     * @param tags     the tags
     *                 标签(有些mq不支持,例如kafka)
     * @param keys     the keys
     *                 关键词(有些mq不支持)
     * @param msg      the msg
     *                 消息内容
     * @param delay    the delay
     *                 延迟等级(有些mq不支持,例如kafka)
     * @param callback the callback
     *                 回调接口
     * @throws MqException the mq exception
     */
    @Override
    public void asyncSend(String topic, String tags, String keys, Object msg,
                              int delay, MqResultCallback callback) throws MqException {
        throw new MqException("This is abstract producer so that can not send message");
    }
}

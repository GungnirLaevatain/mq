package com.github.gungnirlaevatain.mq.producer;

import com.github.gungnirlaevatain.mq.MqException;

public interface Producer {
    /**
     * Start.
     * 启动
     */
    void start();

    /**
     * Start.
     * 初始化
     *
     * @param producerProperty the producer property
     *                         配置文件
     */
    void init(ProducerProperty producerProperty);

    /**
     * Shutdown.
     * 关闭
     */
    void shutdown();

    /**
     * Send.
     * 发送消息
     *
     * @param topic the topic
     *              话题
     * @param msg   the msg
     *              消息内容
     * @return the mq result
     * @throws MqException the mq exception
     */
    default MessageSendResult send(String topic, Object msg) throws MqException {
        return send(topic, null, msg);
    }

    /**
     * Send.
     * 发送消息
     *
     * @param topic the topic
     *              话题
     * @param tags  the tags
     *              标签(有些mq不支持,例如kafka)
     * @param msg   the msg
     *              消息内容
     * @return the mq result
     * @throws MqException the mq exception
     */
    default MessageSendResult send(String topic, String tags, Object msg) throws MqException {
        return send(topic, tags, null, msg);
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
     * @return the mq result
     * @throws MqException the mq exception
     */
    default MessageSendResult send(String topic, String tags, String keys, Object msg) throws MqException {
        return send(topic, tags, keys, msg, 0);
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
    MessageSendResult send(String topic, String tags, String keys, Object msg, int delay) throws MqException;

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
    default void asyncSend(String topic, Object msg, MqResultCallback callback) throws MqException {
        asyncSend(topic, null, msg, callback);
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
    default void asyncSend(String topic, String tags, Object msg, MqResultCallback callback) throws MqException {
        asyncSend(topic, tags, null, msg, callback);
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
    default void asyncSend(String topic, String tags, String keys,
                           Object msg, MqResultCallback callback) throws MqException {
        asyncSend(topic, tags, keys, msg, 0, callback);
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
    void asyncSend(String topic, String tags, String keys,
                   Object msg, int delay, MqResultCallback callback) throws MqException;

}

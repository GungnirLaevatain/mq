

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
    MessageSendResult send(final String topic, final Object msg) throws MqException;

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
    MessageSendResult send(final String topic, final String tags, final Object msg) throws MqException;

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
    MessageSendResult send(final String topic, final String tags, final String keys, final Object msg) throws MqException;

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
    MessageSendResult send(final String topic, final String tags, final String keys, final Object msg, final int delay) throws MqException;

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
    void asyncSend(final String topic, final Object msg, final MqResultCallback callback) throws MqException;

    /**
     * Async Send.
     * 发送消息
     *
     * @param topic the topic
     *              话题
     * @param tags  the tags
     *              标签(有些mq不支持,例如kafka)
     * @param msg   the msg
     *              消息内容
     * @param callback the callback
     *                 回调接口
     * @throws MqException the mq exception
     */
    void asyncSend(final String topic, final String tags, final Object msg, final MqResultCallback callback) throws MqException;

    /**
     * Async Send.
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
     * @param callback the callback
     *                 回调接口
     * @throws MqException the mq exception
     */
    void asyncSend(final String topic, final String tags, final String keys,
                   final Object msg, final MqResultCallback callback) throws MqException;

    /**
     * Async Send.
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
     * @param callback the callback
     *                 回调接口
     * @throws MqException the mq exception
     */
    void asyncSend(final String topic, final String tags, final String keys,
                   final Object msg, final int delay, final MqResultCallback callback) throws MqException;

}

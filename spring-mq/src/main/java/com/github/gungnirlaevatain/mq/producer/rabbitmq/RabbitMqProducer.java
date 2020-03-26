
package com.github.gungnirlaevatain.mq.producer.rabbitmq;

import com.github.gungnirlaevatain.mq.MqException;
import com.github.gungnirlaevatain.mq.producer.AbstractProducer;
import com.github.gungnirlaevatain.mq.producer.MessageSendResult;
import com.github.gungnirlaevatain.mq.producer.MqResultCallback;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class RabbitMqProducer extends AbstractProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public MessageSendResult send(String exchange, Object msg) throws MqException {
        return send(exchange, null, exchange, msg);
    }

    @Override
    public MessageSendResult send(String exchange, String routekey, Object msg) throws MqException {
        return send(exchange, null, routekey, msg);
    }

    @Override
    public MessageSendResult send(String exchange, String tags, String routekey, Object msg) throws MqException {
        try {
            rabbitTemplate.convertAndSend(exchange, routekey, msg);
        } catch (AmqpException e) {
            throw new MqException(e);
        }
        MessageSendResult result = new MessageSendResult();

        result.setSuccess(true);
        return result;
    }

    /**
     * 假异步
     */
    @Override
    public void asyncSend(String exchange, Object msg, MqResultCallback callback) throws MqException {
        asyncSend(exchange, null, exchange, msg, callback);
    }

    @Override
    public void asyncSend(String exchange, String routekey, Object msg, MqResultCallback callback) throws MqException {
        asyncSend(exchange, null, routekey, msg, callback);
    }

    @Override
    public void asyncSend(String exchange, String tags, String routekey, Object msg, MqResultCallback callback) throws MqException {
        try {
            MessageSendResult result = send(routekey, tags, routekey, msg);
            callback.success(result);
        } catch (Exception e) {
            callback.fail(e);
        }
    }

}





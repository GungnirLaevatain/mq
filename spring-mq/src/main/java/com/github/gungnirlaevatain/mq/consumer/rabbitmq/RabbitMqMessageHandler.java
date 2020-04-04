package com.github.gungnirlaevatain.mq.consumer.rabbitmq;

import com.github.gungnirlaevatain.mq.consumer.AbstractMqMessageHandler;
import com.github.gungnirlaevatain.mq.consumer.MessageRecord;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.MessageConverter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
@Data
public class RabbitMqMessageHandler extends AbstractMqMessageHandler<Message> {

    private MessageConverter messageConverter;

    public RabbitMqMessageHandler(Object bean, Method method, MessageConverter messageConverter) {
        super(bean, method);
        this.messageConverter = messageConverter;
    }

    /**
     * 绑定2个是为了有2个routeKey，一个为exchange的name,一个为queue的name
     * 这样2个调用才能生效
     *
     * @see RabbitMqProducer#send(String, Object)
     * @see RabbitMqProducer#send(String, String, Object)
     */
    @RabbitListener(bindings = {
            @QueueBinding(value = @Queue(durable = "true"), exchange = @Exchange(value = "", durable = "true")),
            @QueueBinding(value = @Queue(durable = "true"), exchange = @Exchange(value = "", durable = "true"))
    })
    public void listener(Message message) throws InvocationTargetException, IllegalAccessException {
        handleMessage(message);
    }

    @Override
    protected MessageRecord getMessageRecord(Message msg) {
        return new MessageRecord(msg.getMessageProperties().getReceivedExchange(), null, msg.getMessageProperties().getConsumerQueue(),
                msg.getBody(), 0, 0);
    }

    @Override
    protected Object getMessageBody(Message msg) {
        return messageConverter.fromMessage(msg);
    }
}


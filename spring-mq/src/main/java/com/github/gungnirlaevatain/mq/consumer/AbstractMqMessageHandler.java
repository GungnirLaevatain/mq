package com.github.gungnirlaevatain.mq.consumer;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
@Data
public abstract class AbstractMqMessageHandler<T> implements MqMessageHandler<T> {

    private Object bean;
    private Method method;
    private Class<?>[] parameterTypes;

    public AbstractMqMessageHandler(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
        parameterTypes = method.getParameterTypes();
        if (parameterTypes.length > 1) {
            throw new IllegalArgumentException("the @MqListener method parameter num can only one!,method is " + method.getName());
        }
    }


    protected void handleMessage(T msg) throws InvocationTargetException, IllegalAccessException {
        log.debug("invoke method received record is {}", msg);
        if (parameterTypes.length == 0) {
            method.invoke(bean);
        } else {
            Object obj = MessageRecord.class != parameterTypes[0] ? getMessageBody(msg) : getMessageRecord(msg);
            method.invoke(bean, obj);
        }
    }

    protected abstract MessageRecord getMessageRecord(T msg);


    protected abstract Object getMessageBody(T msg);

}



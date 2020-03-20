

package com.github.gungnirlaevatain.mq;


import com.github.gungnirlaevatain.mq.consumer.EnableMqConsumer;
import com.github.gungnirlaevatain.mq.producer.EnableMqProducer;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableMqConsumer
@EnableMqProducer
public @interface EnableMq {
}

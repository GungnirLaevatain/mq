

package com.github.gungnirlaevatain.mq.consumer;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ConsumerConfigurationSelector.class})
public @interface EnableMqConsumer {
}



package com.github.gungnirlaevatain.mq.producer;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ProducerConfigurationSelector.class})
public @interface EnableMqProducer {
}

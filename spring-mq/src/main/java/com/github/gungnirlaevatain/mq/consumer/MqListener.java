

package com.github.gungnirlaevatain.mq.consumer;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MqListener {
    String[] topics() default {};

    String[] tags() default {};

    String groupName() default "";

    ListenerType type() default ListenerType.DEFAULT;

    int maxConsumer() default 0;

    int minConsumer() default 0;

}

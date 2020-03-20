
package com.github.gungnirlaevatain.mq.consumer;

import com.github.gungnirlaevatain.mq.MqType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

@Slf4j
public class ConsumerConfigurationSelector implements ImportSelector, EnvironmentAware {

    private Environment environment;

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        MqType mqType = environment.getProperty(ConsumerProperty.PREFIX + ".type", MqType.class);
        if (mqType != null) {
            return new String[]{mqType.getConsumerConfig().getName()};
        } else {
            log.error("{}.type is null. Please define it in the context",ConsumerProperty.PREFIX);
            return new String[0];
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}

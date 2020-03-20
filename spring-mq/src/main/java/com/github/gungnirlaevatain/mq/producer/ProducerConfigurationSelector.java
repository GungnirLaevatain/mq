

package com.github.gungnirlaevatain.mq.producer;

import com.github.gungnirlaevatain.mq.MqType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

@Slf4j
public class ProducerConfigurationSelector implements ImportSelector, EnvironmentAware {
    private Environment environment;

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        MqType mqType = environment.getProperty(ProducerProperty.PREFIX + ".type", MqType.class);
        if (mqType != null) {
            return new String[]{mqType.getProducerConfig().getName()};
        } else {
            log.error("{}.type is null. Please define it in the context",ProducerProperty.PREFIX);
            return new String[0];
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}

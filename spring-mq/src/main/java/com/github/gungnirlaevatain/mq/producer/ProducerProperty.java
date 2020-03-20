
package com.github.gungnirlaevatain.mq.producer;

import com.github.gungnirlaevatain.mq.MqType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = ProducerProperty.PREFIX)
@Data
public class ProducerProperty {
    public static final String PREFIX = "common.mq.producer";
    private MqType type;
}

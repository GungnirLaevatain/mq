

package com.github.gungnirlaevatain.mq.consumer;


import com.github.gungnirlaevatain.mq.MqType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = ConsumerProperty.PREFIX)
@Data
public class ConsumerProperty {
    public static final String PREFIX = "common.mq.consumer";
    private MqType type;
}


package com.github.gungnirlaevatain.mq.consumer.rabbitmq;

import com.github.gungnirlaevatain.mq.consumer.ConsumerProperty;
import lombok.Data;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@ConfigurationProperties(prefix = RabbitMqConsumerProperty.PREFIX)
@Data
public class RabbitMqConsumerProperty {
    public static final String PREFIX = ConsumerProperty.PREFIX + ".rabbitmq";

    /**
     * The Address
     */
    private List<Address> address;

    private String host;

    private int port;

    /**
     * Login user to authenticate to the broker.
     */
    private String username;

    /**
     * Login to authenticate against the broker.
     */
    private String password;

    /**
     * Virtual host to use when connecting to the broker.
     */
    private String virtualHost;

    /**
     * Requested heartbeat timeout, in seconds; zero for none.
     */
    private int requestedHeartbeat;

    /**
     * Enable publisher confirms.
     */
    private boolean publisherConfirms;

    /**
     * Enable publisher returns.
     */
    private boolean publisherReturns;

    /**
     * Connection timeout, in milliseconds; zero for infinite.
     */
    private int connectionTimeout;

    /**
     * Number of channels to retain in the cache. When "check-timeout" > 0, max
     * channels per connection.
     */
    private int channelSize=1;

    /**
     * Number of connections to cache. Only applies when mode is CONNECTION.
     */
    private int connectionSize=1;

    /**
     * Connection factory cache mode.
     */
    private CachingConnectionFactory.CacheMode cacheMode= CachingConnectionFactory.CacheMode.CHANNEL;

    /**
     * Number of milliseconds to wait to obtain a channel if the cache size has
     * been reached. If 0, always create a new channel.
     */
    private long checkoutTimeout;

    private Class<MessageConverter> messageConverter;

    private Class<ConsumerTagStrategy> consumerTagStrategy;

    @NestedConfigurationProperty
    private RabbitProperties.Listener listener=new RabbitProperties.Listener();
    /**
     * Returns the comma-separated addresses or a single address ({@code host:port})
     * created from the configured host and port if no addresses have been set.
     *
     * @return the addresses
     */
    public String determineAddresses() {
        if (CollectionUtils.isEmpty(address)) {
            return host + ":" + port;
        }
        return StringUtils.collectionToCommaDelimitedString(address);
    }

    @Data
    public static class Address {
        private String host;
        private String port;
    }
}

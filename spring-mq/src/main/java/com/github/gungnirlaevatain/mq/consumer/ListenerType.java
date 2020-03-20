
package com.github.gungnirlaevatain.mq.consumer;

public enum ListenerType {

    /**
     * Default listener type.
     * 默认消费方式
     */
    DEFAULT,
    /**
     * Broadcasting listener type.
     * 广播方式消费
     */
    BROADCASTING,
    /**
     * Clustering listener type.
     * 集群方式消费
     */
    CLUSTERING

}

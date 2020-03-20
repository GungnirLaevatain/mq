

package com.github.gungnirlaevatain.mq.producer;


public interface MqResultCallback {

    /**
     * Success.
     * 成功回调
     *
     * @param result the result
     */
    default void success(MessageSendResult result){}

    /**
     * Fail.
     * 异常回调
     *
     * @param t the t
     */
    default void fail(Throwable t){};
}

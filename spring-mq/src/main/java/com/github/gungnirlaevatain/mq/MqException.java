

package com.github.gungnirlaevatain.mq;

public class MqException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MqException() {
        super();
    }

    public MqException(String message) {
        super(message);
    }
    public MqException(Throwable cause) {
        super(cause);
    }
}

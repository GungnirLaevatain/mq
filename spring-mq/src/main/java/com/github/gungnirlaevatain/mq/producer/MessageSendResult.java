package com.github.gungnirlaevatain.mq.producer;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class MessageSendResult {

    private boolean success;
}

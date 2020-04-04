package com.github.gungnirlaevatain.mq.consumer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRecord {

    private String topic;
    private Object keys;
    private String tags;
    private byte[] body;
    private long timestamp;
    private int delay;

}

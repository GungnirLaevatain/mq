package com.github.gungnirlaevatain.mq.producer.kafka;

import com.github.gungnirlaevatain.mq.MqException;
import com.github.gungnirlaevatain.mq.producer.AbstractProducer;
import com.github.gungnirlaevatain.mq.producer.MessageSendResult;
import com.github.gungnirlaevatain.mq.producer.MqResultCallback;
import com.github.gungnirlaevatain.mq.producer.ProducerProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

@Slf4j
@SuppressWarnings("unchecked")
public class KafkaProducer extends AbstractProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Override
    public void start() {
        kafkaTemplate.flush();
        log.info("kafka producer has been start");
    }

    @Override
    public void init(ProducerProperty producerProperty) {
        log.debug("kafka producer has been init");
    }

    @Override
    public void shutdown() {
        log.info("kafka producer has been shutdown");
    }

    @Override
    public MessageSendResult send(String topic, String tags, String keys, Object msg) throws MqException {
        ListenableFuture<SendResult> sendResultListen = kafkaTemplate.send(topic, keys, msg);
        try {
            //设置超时时间?
            SendResult sendResult = sendResultListen.get();
            KafkaSendResult kafkaResult = KafkaSendResult.builder()
                    .producerRecord(sendResult.getProducerRecord())
                    .recordMetadata(sendResult.getRecordMetadata())
                    .build();
            kafkaResult.setSuccess(true);
            return kafkaResult;
        } catch (InterruptedException | ExecutionException e) {
            throw new MqException(e);
        }
    }

    @Override
    public void asyncSend(String topic, String tags, String keys, Object msg, MqResultCallback callback) throws MqException {
        ListenableFuture<SendResult> sendResultListen = kafkaTemplate.send(topic, keys, msg);
        sendResultListen.addCallback(result -> callback.success(createResult(result)), callback::fail);
    }

    private KafkaSendResult createResult(SendResult result) {
        KafkaSendResult kafkaResult = KafkaSendResult.builder()
                .producerRecord(result.getProducerRecord())
                .recordMetadata(result.getRecordMetadata())
                .build();
        kafkaResult.setSuccess(true);
        return kafkaResult;
    }


}

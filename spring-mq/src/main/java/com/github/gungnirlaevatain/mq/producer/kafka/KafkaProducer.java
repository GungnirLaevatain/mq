
package com.github.gungnirlaevatain.mq.producer.kafka;

import com.github.gungnirlaevatain.mq.MqException;
import com.github.gungnirlaevatain.mq.producer.AbstractProducer;
import com.github.gungnirlaevatain.mq.producer.MessageSendResult;
import com.github.gungnirlaevatain.mq.producer.MqResultCallback;
import com.github.gungnirlaevatain.mq.producer.ProducerProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@SuppressWarnings("unchecked")
public class KafkaProducer extends AbstractProducer {
    private KafkaTemplate kafkaTemplate;

    @Override
    public void start() {
        kafkaTemplate.flush();
        log.info("kafka producer has been start");
    }

    @Override
    public void init(ProducerProperty producerProperty) {
        KafkaProducerProperty kafkaProducerProperty = (KafkaProducerProperty) producerProperty;
        this.kafkaTemplate = new KafkaTemplate(producerFactory(kafkaProducerProperty));

        Class<ProducerListener<?,?>> producerListener = kafkaProducerProperty.getProducerListener();
        if (producerListener != null) {
            try {
                this.kafkaTemplate.setProducerListener(producerListener.newInstance());
                log.debug("listener that name is {} start success", producerListener.getName());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new MqException(e);
            }
        } else {
            this.kafkaTemplate.setProducerListener(new DefaultProducerListener());
        }

        Class<RecordMessageConverter> converter = kafkaProducerProperty.getMessageConverter();
        if (converter != null) {
            try {
                this.kafkaTemplate.setMessageConverter(converter.newInstance());
                log.debug("MessageConverter that name is {} start success", converter.getName());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new MqException(e);
            }
        }
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

    private Map<String, Object> producerConfigs(KafkaProducerProperty producerProperty) {
        Map<String, Object> props = new HashMap<>(7);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerProperty.getServer());
        props.put(ProducerConfig.RETRIES_CONFIG, producerProperty.getRetries());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, producerProperty.getBatchSize());
        props.put(ProducerConfig.LINGER_MS_CONFIG, producerProperty.getLingerMs());
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, producerProperty.getBufferMemory());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, producerProperty.getKeySerializerClass());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, producerProperty.getValueSerializerClass());
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, producerProperty.getCompressType());
        return props;
    }

    private ProducerFactory producerFactory(KafkaProducerProperty producerProperty) {
        return new DefaultKafkaProducerFactory<>(producerConfigs(producerProperty));
    }

}

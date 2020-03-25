package com.lyq.demo.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ProducerMain {

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers","localhost:9091");
        properties.put("key.serializer", StringSerializer.class);
        properties.put("value.serializer", StringSerializer.class);
        KafkaProducer kafkaProducer = new KafkaProducer(properties);

        ProducerRecord producerRecord = new ProducerRecord("mytopic","message","hah");

        Future<RecordMetadata> future = kafkaProducer.send(producerRecord);

        RecordMetadata recordMetadata = null;
        try {
            recordMetadata = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(recordMetadata);
    }
}

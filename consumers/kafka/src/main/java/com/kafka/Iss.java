package com.kafka;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class Iss {
    public static void main(String[] args) {
        // Parameters
        String kafkaServer = "localhost:9092";
        String topic = "advanceddb";
        String groupId = "kafka";
        // Setting proprety for kafka
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaServer);
        props.put("group.id", groupId);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        // creating the consumer and trying to get data from the topics
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            // assigning the consumer to the topic
            consumer.subscribe(Collections.singletonList(topic));

            while (true) {
                // trying to poll from the topic with a timeout of 100 ms
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                // print records get
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(),
                            record.value());
                }
            }
        }

    }
}
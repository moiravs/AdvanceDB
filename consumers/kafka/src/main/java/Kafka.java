package com.kafka;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class Kafka {
    public static void main(String[] args) {
        // Parameters
        String kafkaServer = "localhost:9092";
        String topic = "iss";
        String groupId = "kafka";

        Double lastLatitude = null;
        Double lastLongitude = null;
        Integer lastTimestamp = null;
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
                    System.out.println(record.value());
                    Map<String, String> valueMap = new ObjectMapper().readValue(record.value(),
                            new TypeReference<Map<String, String>>() {
                            });

                
        }

    }

}
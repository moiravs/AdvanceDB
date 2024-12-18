package com.kafka;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class Kafka {

    public static void main(String[] args) throws IOException {
        String banListFile = "data/banlist.txt";
        String[] bannedWords = Files.readAllLines(Paths.get(banListFile)).toArray(String[]::new);
        try (KafkaConsumer<String, String> consumer = connectToBroker()) {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    Utils.processMessage(record.value(), bannedWords);
                }
            }
        }
    }

    /**
     * Connect to the Kafka broker
     * 
     * @return a Kafka consumer
     */
    public static KafkaConsumer<String, String> connectToBroker() {
        String kafkaServer = "localhost:9092";
        String topic = "chat";
        String groupId = "kafka";

        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaServer);
        props.put("group.id", groupId);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
        return consumer;
    }
}

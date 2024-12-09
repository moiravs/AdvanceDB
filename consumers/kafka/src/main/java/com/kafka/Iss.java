package com.kafka;

import java.lang.classfile.instruction.ThrowInstruction;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.source.doctree.SerialFieldTree;

public class Iss {
    public static void main(String[] args) throws JsonProcessingException {
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
                    Map<String, String> valueMap = new ObjectMapper().readValue(record.value(),
                            new TypeReference<Map<String, String>>() {
                            });
                    Double latitude = Double.valueOf(valueMap.get("latitude"));
                    Double longitude = Double.valueOf(valueMap.get("longitude"));
                    Integer timestamp = Integer.valueOf(valueMap.get("timestamp")); // secondes

                    if (lastLatitude != null && lastLongitude != null && lastTimestamp != null) {
                        Double speed;
                        speed = calculateSpeed(lastLatitude, lastLongitude, lastTimestamp, latitude, longitude,
                                timestamp);
                        System.out.println("Speed: " + speed + " km/h");
                    }

                    lastLatitude = latitude;
                    lastLongitude = longitude;
                    lastTimestamp = timestamp;

                }
            }
        }

    }

    // source: https://www.baeldung.com/java-find-distance-between-points
    static double haversine(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }

    public static Double calculateSpeed(Double startLong, Double endLong, Integer timestamp1, Double startLat,
            Double endLat, Integer timestamp2) {
        // Haversine formula
        double R = 6371; // Radius of the earth in km

        double dLat = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);

        double a = haversine(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversine(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double timeDiff = (timestamp2 - timestamp1); // in seconds

        return distance / (timeDiff / 3600); // in km/h
        // 1 h = 3600 s

    }
}
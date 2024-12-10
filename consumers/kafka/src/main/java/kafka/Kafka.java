package kafka;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

public class Kafka {

    public static void main(String[] args) throws JsonProcessingException, IOException {
        // Parameters
        String banListFile = "../../banlist.txt";
        String[] bannedWords = Files.readAllLines(Paths.get(banListFile)).toArray(String[]::new);
        try (KafkaConsumer<String, String> consumer = connectToBroker()) {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    Map<String, String> valueMap = new ObjectMapper().readValue(record.value(),
                            new TypeReference<Map<String, String>>() {
                            });
                    if (checkWordBan(valueMap.get("text"), bannedWords)) {
                        System.out.println("\u001B[31m" +
                                valueMap.get("date") + " || " + valueMap.get("user") + ": Message contains banned word"
                                + "\u001B[0m");
                        return;
                    }
                    System.out.println(
                            valueMap.get("date") + " || " + valueMap.get("user") + ": " + valueMap.get("text"));
                }
            }
        }
    }

    public static boolean checkWordBan(String text, String[] bannedWords) {
        for (String word : bannedWords) {
            if (text.contains(word)) {
                return true;
            }
        }
        return false;

    }

    public static KafkaConsumer<String, String> connectToBroker() {
        String kafkaServer = "localhost:9092";
        String topic = "chat";
        String groupId = "kafka";

        // Setting proprety for kafka
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

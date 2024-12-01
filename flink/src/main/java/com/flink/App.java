package com.flink;

import java.util.Properties;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

public class App {
    public static void main(String[] args) throws Exception {
        // Connect to kafka
        String kafkaServer = "localhost:9092";
        String topic = "advanceddb";
        String groupId = "flink";
        StreamExecutionEnvironment environment = StreamExecutionEnvironment
                .getExecutionEnvironment();

        FlinkKafkaConsumer<String> kafkaConsumer = createStringConsumerForTopic(topic, kafkaServer, groupId);

        DataStream<String> stringInputStream = environment.addSource(kafkaConsumer);

    }

    public static FlinkKafkaConsumer<String> createStringConsumerForTopic(
            String topic, String kafkaAddress, String kafkaGroup) {

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", kafkaAddress);
        props.setProperty("group.id", kafkaGroup);
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(
                topic, new SimpleStringSchema(), props);

        return consumer;
    }

}
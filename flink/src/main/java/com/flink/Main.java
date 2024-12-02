package com.flink;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;

public class Main {
    public static void main(String[] args) throws Exception {
        // Connect to kafka
        String kafkaServer = "localhost:9092";
        String topic = "advanceddb";
        String groupId = "flink";
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        KafkaSource<String> kafkaSource = createStringConsumerForTopic(topic, kafkaServer, groupId);

        DataStream<String> stringInputStream = environment.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(),
                "Kafka Source");

        // Print the stream
        SinkFunction<String> sink = new SinkFunction<String>() {
            @Override
            public void invoke(String value, Context context) throws Exception {
                System.out.println(value);
            }
        };
        stringInputStream.addSink(sink);

        environment.execute("Flink Kafka Consumer");
    }

    public static KafkaSource<String> createStringConsumerForTopic(
            String topic, String kafkaAddress, String kafkaGroup) {
        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers(kafkaAddress)
                .setTopics(topic)
                .setGroupId(kafkaGroup)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        return source;
    }

}
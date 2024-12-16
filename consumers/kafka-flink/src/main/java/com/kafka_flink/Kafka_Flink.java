package com.kafka_flink;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public class Kafka_Flink {
    public static void main(String[] args) throws IOException {
        String kafkaServer = "localhost:9092";
        String topic = "chat";
        String groupId = "flink";
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        environment.enableCheckpointing(1000);
        environment.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        environment.getCheckpointConfig().setMinPauseBetweenCheckpoints(500);
        environment.getCheckpointConfig().setCheckpointTimeout(60000);
        KafkaSource<String> kafkaSource = createStringConsumerForTopic(topic, kafkaServer, groupId);
        DataStream<String> stringInputStream = environment.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(),
                "Kafka Source");
        String banListFile = "../../banlist.txt";
        String[] banWords = Files.readAllLines(Paths.get(banListFile)).toArray(String[]::new);
        SinkFunction<String> sink = new SinkFunction<String>() {

            @Override
            public void invoke(String value, Context context) throws Exception {
                Utils.processMessage(value, banWords);
            }
        };
        stringInputStream.addSink(sink);
        try {
            environment.execute("Flink Consumer");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a Kafka source for a given topic
     * 
     * @param topic        the topic to consume
     * @param kafkaAddress the address of the Kafka broker
     * @param kafkaGroup   the Kafka consumer group
     * @return a Kafka source
     */
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
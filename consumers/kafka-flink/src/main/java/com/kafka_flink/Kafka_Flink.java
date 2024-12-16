package com.kafka_flink;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Kafka_Flink {
    public static void main(String[] args) throws IOException {
        // kafla topic information

        String kafkaServer = "localhost:9092";
        String topic = "chat";
        String groupId = "flink";
        // create a flink environment
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        // Enable checkpointing (commit offsets during checkpoints)
        environment.enableCheckpointing(1000); // Checkpoint every 1000 ms (1 second)
        environment.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE); // Ensure exactly-once
                                                                                                // semantics
        environment.getCheckpointConfig().setMinPauseBetweenCheckpoints(500); // Pause between checkpoints
        environment.getCheckpointConfig().setCheckpointTimeout(60000); // Timeout for checkpoint

        // creating special kafka consumer for flink
        KafkaSource<String> kafkaSource = createStringConsumerForTopic(topic, kafkaServer, groupId);
        // set kafka source as a source for flink
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
        // set the handler for results
        stringInputStream.addSink(sink);
        // start the flink environment to start getting records from kafka source and
        // print them on the console.
        try {
            environment.execute("Flink Consumer");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Function used to create a kafka consumer specialy for flink
     *
     **/
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
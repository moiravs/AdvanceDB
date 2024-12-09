package com.iss;

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

public class Iss {
    public static void main(String[] args) throws Exception {
        // kafla topic information
        String kafkaServer = "localhost:9092";
        String topic = "iss";
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

        SinkFunction<String> sink = new SinkFunction<String>() {
            Double lastLatitude = null;
            Double lastLongitude = null;
            Integer lastTimestamp = null;

            @Override
            public void invoke(String value, Context context) throws Exception {
                Map<String, String> valueMap = new ObjectMapper().readValue(value,
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
        };
        // set the handler for results
        stringInputStream.addSink(sink);
        // start the flink environment to start getting records from kafka source and
        // print them on the console.
        environment.execute("Flink Kafka Consumer");
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

        double distance = R * c / 1000; // in km

        double timeDiff = (timestamp2 - timestamp1); // in seconds

        return distance / (timeDiff / 3600); // in km/h
        // 1 h = 3600 s

    }

}
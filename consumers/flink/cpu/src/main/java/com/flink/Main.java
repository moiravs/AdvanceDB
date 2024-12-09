import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

public class KafkaFlinkExample {

    public static void main(String[] args) throws Exception {
        // Step 1: Set up the Flink execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Step 2: Configure Kafka consumer properties
        Properties kafkaProperties = new Properties();
        kafkaProperties.setProperty("bootstrap.servers", "localhost:9092");
        kafkaProperties.setProperty("group.id", "flink-consumer-group");

        // Step 3: Create a Kafka consumer with custom deserialization schema
        FlinkKafkaConsumer<Integer> kafkaConsumer = new FlinkKafkaConsumer<>(
                "cpu",
                new IntegerDeserializationSchema(),
                kafkaProperties
        );

        // Step 4: Add Kafka consumer as a source to Flink
        DataStream<Integer> kafkaStream = env.addSource(kafkaConsumer);

        // Step 5: Process the stream (divide by 1000 and print)
        kafkaStream
                .map(value -> value / 1000)
                .print();

        // Step 6: Execute the Flink job
        env.execute("Kafka to Flink CPU Example");
    }
}


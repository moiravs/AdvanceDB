package com.flink;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class App {
    public static void main(String[] args) throws Exception {
        // Set up the execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Create a DataStream from a socket source
        DataStream<String> text = env.socketTextStream("localhost", 9999);

        // Process the data
        DataStream<String> processed = text.map(new MapFunction<String, String>() {
            @Override
            public String map(String value) throws Exception {
                return "Processed: " + value;
            }
        });

        // Print the processed data to the console
        processed.print();

        // Execute the Flink job
        env.execute("Flink Stream DB Example");
    }
}
package com.flink;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

public class Flink {
    public static void main(String[] args) {
        // HTTP server URL
        String urlString = "http://localhost:5000/";
		System.out.println("URL: " + urlString);

        try {
            // Create Flink environment
            StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();

            // Enable checkpointing with configurations
            environment.enableCheckpointing(1000); // 1 second
            environment.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
            environment.getCheckpointConfig().setMinPauseBetweenCheckpoints(500); // 0.5 second
            environment.getCheckpointConfig().setCheckpointTimeout(60000); // 1 minute

            // Load banned words from file
            String banListFile = "../../banlist.txt";
            List<String> banWords = Files.readAllLines(Paths.get(banListFile));

            // Create a custom source
            DataStream<String> stringInputStream = environment.addSource(new HttpSource(urlString));

            // Define a sink to process messages
            SinkFunction<String> sink = new SinkFunction<>() {
                @Override
                public void invoke(String value, Context context) {
                    try {
                        System.out.println("Processing message: " + value);
                        Utils.processMessage(value, banWords.toArray(new String[0]));
                    } catch (Exception e) {
                        System.err.println("Error processing message: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            };

            // Add sink to the stream
            stringInputStream.addSink(sink);

            // Execute the Flink job
            System.out.println("Starting Flink job...");
            environment.execute("Flink Consumer");
			System.out.println("Flink job started.");
        } catch (Exception e) {
            System.err.println("Failed to execute Flink job: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

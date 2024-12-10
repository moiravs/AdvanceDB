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
    public static void main(String[] args) throws Exception {
        // HTTP server information
        String urlString = "http://localhost:5000/stream";

        // Create a Flink environment
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        // Enable checkpointing (commit offsets during checkpoints)
        environment.enableCheckpointing(1000); // Checkpoint every 1000 ms (1 second)
        environment.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE); // Ensure exactly-once
                                                                                                // semantics
        environment.getCheckpointConfig().setMinPauseBetweenCheckpoints(500); // Pause between checkpoints
        environment.getCheckpointConfig().setCheckpointTimeout(60000); // Timeout for checkpoint

        // Create a DataStream by reading from an HTTP server
        DataStream<String> stringInputStream = environment.addSource(new HttpSource(urlString));

        // Load banned words from file
        String banListFile = "../../banlist.txt";
        List<String> banWords = Files.readAllLines(Paths.get(banListFile));

        // Define a sink function to process messages
        SinkFunction<String> sink = new SinkFunction<String>() {
            @Override
            public void invoke(String value, Context context) throws Exception {
                try {
                    System.out.println("Processing message: " + value);
                    Utils.processMessage(value, banWords.toArray(new String[0]));
                } catch (Exception e) {
                    System.err.println("Error processing message: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        // Set the handler for results
        stringInputStream.addSink(sink);

        // Start the Flink environment to start getting records from the HTTP server and
        // print them on the console
        try {
            System.out.println("Starting Flink job...");
            environment.execute("Flink Consumer");
        } catch (Exception e) {
            System.err.println("Failed to execute Flink job: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Define a custom source function to read from an HTTP server
    public static class HttpSource implements SourceFunction<String> {
        private final String urlString;
        private volatile boolean isRunning = true;

        public HttpSource(String urlString) {
            System.out.println("Creating HTTP source...");
            this.urlString = urlString;
        }

        @Override
        public void run(SourceContext<String> ctx) throws Exception {
            System.out.println("Reading from HTTP server: " + urlString);
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while (isRunning && (line = reader.readLine()) != null) {
                    System.out.println("Received message: " + line);
                    ctx.collect(line);
                }
            } catch (Exception e) {
                System.err.println("Error reading from HTTP server: " + e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public void cancel() {
            System.out.println("Cancelling HTTP source...");
            isRunning = false;
        }
    }
}
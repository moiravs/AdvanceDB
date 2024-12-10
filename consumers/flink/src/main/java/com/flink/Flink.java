package com.flink;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

public class Flink {
    public static void main(String[] args) throws IOException {
        // Socket server information
        String hostname = "localhost";
        int port = 9999;

        // Create a Flink environment
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        // Enable checkpointing (commit offsets during checkpoints)
        environment.enableCheckpointing(1000); // Checkpoint every 1000 ms (1 second)
        environment.getCheckpointConfig().setMinPauseBetweenCheckpoints(500); // Pause between checkpoints
        environment.getCheckpointConfig().setCheckpointTimeout(60000); // Timeout for checkpoint
        String banListFile = "../../banlist.txt";
        String[] banWords = Files.readAllLines(Paths.get(banListFile)).toArray(String[]::new);

        // Create a socket text stream source
        DataStream<String> text = environment.socketTextStream(hostname, port, "\n");

        SinkFunction<String> sink = new SinkFunction<String>() {

            @Override
            public void invoke(String value, Context context) throws Exception {
                Utils.processMessage(value, banWords);
            }
        };
        // set the handler for results
        text.addSink(sink);
        // start the flink environment to start getting records from kafka source and
        // print them on the console.
        try {
            environment.execute("Flink Consumer");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
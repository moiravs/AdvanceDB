package com.flink;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Flink {
    public static void main(String[] args) throws Exception {
        // Setup the execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Path to the input files (adjust as needed)
        String inputFilePath = "../../chat.csv";
        String banListFilePath = "../../banlist.txt";
        String acceptedOutputPath = "accepted-messages.csv";
        String bannedOutputPath = "banned-messages.csv";

        // Read and parse the banlist file into a set for quick lookup
        Set<String> banList = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(banListFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                banList.add(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            System.err.println("Error reading the banlist file: " + e.getMessage());
            return;
        }

        // Read the input file into a DataStream
        DataStream<String> messages = env.readTextFile(inputFilePath);

        // Process the messages and filter them
        DataStream<Tuple2<String, String>> processedMessages = messages.flatMap(new FlatMapFunction<String, Tuple2<String, String>>() {
            @Override
            public void flatMap(String message, Collector<Tuple2<String, String>> out) {
                // Check if any word in the message is in the banlist
                String[] words = message.toLowerCase().split("\\s+");
                for (String word : words) {
                    if (banList.contains(word)) {
                        // Banned word found
                        out.collect(new Tuple2<>("banned", message));
                        return;
                    }
                }
                // If no banned words found, it's accepted
                out.collect(new Tuple2<>("accepted", message));
            }
        });

        // Split the stream into accepted and banned messages
        DataStream<String> acceptedMessages = processedMessages
                .filter(tuple -> "accepted".equals(tuple.f0))
                .map(tuple -> tuple.f1);

        DataStream<String> bannedMessages = processedMessages
                .filter(tuple -> "banned".equals(tuple.f0))
                .map(tuple -> tuple.f1);

        // Write the results to separate files
        acceptedMessages.writeAsText(acceptedOutputPath, FileSystem.WriteMode.OVERWRITE);
        bannedMessages.writeAsText(bannedOutputPath, FileSystem.WriteMode.OVERWRITE);

        // Execute the Flink job
        env.execute("Message Filter Job");
    }
}


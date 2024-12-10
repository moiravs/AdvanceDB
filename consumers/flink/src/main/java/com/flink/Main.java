package com.flink;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.common.serialization.Encoder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws Exception {
        // Set up the execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Read the ban list into a Set
        Set<String> banList = loadBanList("../../banlist.txt");

        // Read the CSV file into a DataStream
        DataStream<String> messages = env.readTextFile("../../chat.csv");

        // Filter messages based on the 6th column (index 5)
        DataStream<String> acceptedMessages = messages.filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String line) throws Exception {
                // Split the line by comma
                String[] columns = line.split(",");
                if (columns.length >= 6) {
                    String message = columns[5]; // Get the 6th column (index 5)
                    // Check if the message contains any word in the ban list
                    for (String word : banList) {
                        if (message.contains(word)) {
                            return false; // Message is banned
                        }
                    }
                    return true; // Message is accepted
                }
                return false; // Invalid line
            }
        });

        // Filter banned messages
        DataStream<String> bannedMessages = messages.filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String line) throws Exception {
                String[] columns = line.split(",");
                if (columns.length >= 6) {
                    String message = columns[5];
                    for (String word : banList) {
                        if (message.contains(word)) {
                            return true; // Message is banned
                        }
                    }
                }
                return false; // Message is not banned
            }
        });

        // Write accepted and banned messages to separate output files
        acceptedMessages.writeAsText("accepted-messages.csv", FileSystem.WriteMode.OVERWRITE)
                        .setParallelism(1); // Optional: set parallelism for single-threaded output

        bannedMessages.writeAsText("banned-messages.csv", FileSystem.WriteMode.OVERWRITE)
                       .setParallelism(1); // Optional: set parallelism for single-threaded output

        // Execute the Flink job
        env.execute("Message Filtering Job");
    }

    private static Set<String> loadBanList(String path) throws IOException {
        Set<String> banList = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            String line;
            while ((line = br.readLine()) != null) {
                banList.add(line.trim()); // Add each line as a banned word
            }
        }
        return banList;
    }
}


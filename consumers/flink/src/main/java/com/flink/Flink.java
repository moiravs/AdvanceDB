package com.flink;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;

public class Flink {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Set<String> banList = loadBanList("data/banlist.txt");
        DataStream<Set<String>> banListStream = env.fromElements(banList);
        MapStateDescriptor<Void, Set<String>> broadcastStateDescriptor = new MapStateDescriptor<>("banList", Void.class,
                (Class<Set<String>>) (Class<?>) Set.class);

        BroadcastStream<Set<String>> broadcastBanList = banListStream.broadcast(broadcastStateDescriptor);

        DataStream<String> messages = env.readTextFile("data/chat.csv");
        DataStream<Tuple2<String, Boolean>> processedMessages = messages
                .connect(broadcastBanList)
                .process(new BroadcastProcessFunction<String, Set<String>, Tuple2<String, Boolean>>() {
                    private transient Set<String> banList;
                    private transient Set<String> banUserList;

                    @Override
                    public void processElement(String message, ReadOnlyContext ctx,
                            Collector<Tuple2<String, Boolean>> out) {
                        try {
                            String[] columns = message.split(",");
                            if (columns.length >= 6) {
                                String content = columns[5];
                                String user = columns[4];
                                String date = columns[2];
                                boolean isUserBanned = banUserList.stream().anyMatch(user::equals);
                                boolean isBanned = banList.stream().anyMatch(content::contains);
                                writeMessage(user, content, date, isBanned, isUserBanned);
                                out.collect(Tuple2.of(message, !isBanned)); // true = accepted, false = banned
                            }
                        } catch (Exception e) {
                            System.err.println("Error processing message: " + e.getMessage());
                        }
                    }

                    @Override
                    public void processBroadcastElement(Set<String> value, Context ctx,
                            Collector<Tuple2<String, Boolean>> out) {
                        banList = value;
                        banUserList = new HashSet<>();

                    }

                    public void writeMessage(String user, String message, String date, boolean isBanned,
                            boolean isUserBanned) {
                        if (isUserBanned) {
                            System.out.println("\u001B[31m" +
                                    date + " || " + user + ": User Banned"
                                    + "\u001B[0m");
                        } else if (isBanned) {
                            System.out.println("\u001B[31m" +
                                    date + " || " + user + ": Message contains banned word"
                                    + "\u001B[0m");
                            banUserList.add(user);
                        } else {
                            System.out.println(date + " || " + user + ": " + message);
                        }
                    }
                });

        processedMessages
                .filter(tuple -> tuple.f1) // Accepted
                .map(tuple -> tuple.f0)
                .writeAsText("accepted-messages.csv", FileSystem.WriteMode.OVERWRITE).setParallelism(1);

        processedMessages
                .filter(tuple -> !tuple.f1) // Banned
                .map(tuple -> tuple.f0)
                .writeAsText("banned-messages.csv", FileSystem.WriteMode.OVERWRITE).setParallelism(1);

        env.execute("Message Filtering Job");
    }

    /**
     * Load the ban list from a file
     * 
     * @param path the path to the ban list file
     * @return a set of banned words
     * @throws IOException if an I/O error occurs
     */
    private static Set<String> loadBanList(String path) throws IOException {
        Set<String> banList = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            String line;
            while ((line = br.readLine()) != null) {
                banList.add(line.trim());
            }
        }
        return banList;
    }
}

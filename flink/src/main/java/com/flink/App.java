package com.flink;

import java.net.URL;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple18;

public class App {
    public static void main(String[] args) throws Exception {
        try {
            DataSet<Tuple18<String, String, String, Double, String, String, String, String, String, String, String, String, String, Integer, Integer, String, String, String>> csvInput = App
                    .readCsv("data/loan_transactions.csv");

            System.out.println("Starting benchmark");
            App.benchmark(100000, csvInput);

        } catch (Exception e) {
            System.out.println("Error executing Flink job" + e);
            throw e;
        }
    }

    public static DataSet<Tuple18<String, String, String, Double, String, String, String, String, String, String, String, String, String, Integer, Integer, String, String, String>> readCsv(
            String path) throws Exception {
        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        URL url = App.class.getClassLoader().getResource(path);
        if (url == null) {
            throw new RuntimeException("CSV file not found in resources directory");
        }

        // Read the CSV file
        DataSet<Tuple18<String, String, String, Double, String, String, String, String, String, String, String, String, String, Integer, Integer, String, String, String>> csvInput = env
                .readCsvFile(url.toURI().toString())
                .ignoreFirstLine()
                .parseQuotedStrings('"')
                .includeFields("111111111111111111") // Include all fields
                .types(String.class, String.class, String.class, Double.class, String.class, String.class,
                        String.class, String.class, String.class, String.class, String.class, String.class,
                        String.class, Integer.class, Integer.class, String.class, String.class, String.class);

        return csvInput;
    }

    public static void benchmark(Integer number,
            DataSet<Tuple18<String, String, String, Double, String, String, String, String, String, String, String, String, String, Integer, Integer, String, String, String>> csvInput)
            throws Exception {
        DataSet<Tuple18<String, String, String, Double, String, String, String, String, String, String, String, String, String, Integer, Integer, String, String, String>> limitedCsvInput = csvInput
                .first(number);

        // Process the data

        // Time the data processing
        long startTime = System.currentTimeMillis();

        DataSet<String> processed = limitedCsvInput.map((
                Tuple18<String, String, String, Double, String, String, String, String, String, String, String, String, String, Integer, Integer, String, String, String> value) -> "Processed: "
                        + value);

        processed.print();

        System.out
                .println("Time taken for " + number + " requests: " + (System.currentTimeMillis() - startTime) / 1000.0
                        + " seconds");

    }

}
package com.flink;

import java.net.URL;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple18;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        try {
            // Set up the execution environment
            LOG.info("Setting up the execution environment...");
            final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

            LOG.info("Reading the CSV file...");
            // Access the CSV file from the resources directory
            URL url = App.class.getClassLoader().getResource("data/loan_transactions.csv");
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

            LOG.info("Processing the data...");
            // Process the data
            DataSet<String> processed = csvInput.map((
                    Tuple18<String, String, String, Double, String, String, String, String, String, String, String, String, String, Integer, Integer, String, String, String> value) -> "Processed: "
                            + value);

            LOG.info("Printing the processed data to the console...");
            // Print the processed data to the console
            processed.print();
        } catch (Exception e) {
            LOG.error("Error executing Flink job", e);
            throw e;
        }
    }
}
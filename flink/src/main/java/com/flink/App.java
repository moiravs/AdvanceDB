package com.flink;

import java.net.URL;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple3;
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

            URL url = App.class.getClassLoader().getResource("data/test.csv");

            DataSet<Tuple3<String, String, Integer>> csvInput = env
                    .readCsvFile(url.toURI().toString())
                    .ignoreFirstLine()
                    .parseQuotedStrings('"')
                    .types(String.class, String.class, Integer.class);

            LOG.info("Processing the data...");
            // Process the data
            DataSet<String> processed = csvInput.map((Tuple3<String, String, Integer> value) -> {
                LOG.info("Processing row: " + value);
                return "Processed: " + value;
            });

            LOG.info("Printing the processed data to the console...");
            // Print the processed data to the console
            processed.print();
        } catch (Exception e) {
            LOG.error("Error executing Flink job", e);
            throw e;
        }
    }
}
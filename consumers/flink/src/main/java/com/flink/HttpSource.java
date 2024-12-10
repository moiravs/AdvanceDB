package com.flink;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpSource implements SourceFunction<String> {
    private final String urlString;
    private volatile boolean isRunning = true;

    public HttpSource(String urlString) {
        this.urlString = urlString;
    }

    @Override
    public void run(SourceContext<String> ctx) throws Exception {
		System.out.println("Running HTTP source: " + urlString);
        while (isRunning) {
            try {
                // Create a connection to the URL
                HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000); // Set a timeout for the connection
                connection.setReadTimeout(5000); // Set a timeout for reading data

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("Received data: " + line); // Print the data received
                    ctx.collect(line); // Collect the data for Flink processing
                }
                reader.close();
                connection.disconnect();
            } catch (Exception e) {
                System.err.println("Error while fetching data from the HTTP server: " + e.getMessage());
                e.printStackTrace();
                // Optionally, you could add a sleep here if you want to avoid spamming requests on failure
                Thread.sleep(1000);
            }
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}


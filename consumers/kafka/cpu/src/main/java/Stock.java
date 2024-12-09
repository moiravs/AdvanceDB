package com.kafka;

import java.awt.Color;
import java.awt.Dimension;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Cpu {
    private static Map<String, TimeSeries> seriesMap = new HashMap<>();
    private static TimeSeriesCollection dataset = new TimeSeriesCollection();

    public static void main(String[] args) {
        // Kafka consumer configuration
        String kafkaServer = "localhost:9092";
        String topic = "advanceddb";
        String groupId = "kafka";

        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaServer);
        props.put("group.id", groupId);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        // Predefine the stock symbols you expect to receive
        String[] stockSymbols = { "HBARFDUSD", "MEMEUSDC", "TROYUSDC", "WLDEUR", "1MBABYDOGEUSDC", "CETUSUSDC",
                "COWUSDC", "DYDXUSDC", "HMSTRUSDC", "TURBOUSDC" };

        // Initialize TimeSeries for all expected stock symbols
        for (String symbol : stockSymbols) {
            TimeSeries series = new TimeSeries(symbol);
            seriesMap.put(symbol, series);
            dataset.addSeries(series);
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Stock Market Chart");

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(createChartPanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(topic));

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, String> record : records) {
                    String value = record.value();
                    try {
                        JsonObject jsonObject = JsonParser.parseString(value).getAsJsonObject();
                        String symbol = jsonObject.get("symbol").getAsString(); // Assuming the symbol is in the
                                                                                // "symbol" field
                        double priceChange = jsonObject.get("priceChange").getAsDouble(); // Assuming the price change
                                                                                          // is in the "priceChange"
                                                                                          // field
                        long timestamp = System.currentTimeMillis() / 1000; // Use current time as timestamp

                        // Get the TimeSeries for the stock symbol
                        TimeSeries series = seriesMap.get(symbol);

                        // Add data to the series
                        if (series != null) {
                            series.addOrUpdate(new Second(new java.util.Date(timestamp * 1000)), priceChange);
                        }
                    } catch (Exception e) {
                        System.err.println("Error parsing record: " + value);
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static JPanel createChartPanel() {
        String chartTitle = "Stock Market Data";
        String xAxisLabel = "Time";
        String yAxisLabel = "Price Change";

        JFreeChart chart = ChartFactory.createTimeSeriesChart(chartTitle, xAxisLabel, yAxisLabel, dataset);

        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.BLACK);

        // Customize the renderer to ensure lines are drawn correctly
        renderer.setDefaultShapesVisible(false);
        renderer.setDrawSeriesLineAsPath(true);

        return new ChartPanel(chart) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(800, 600);
            }
        };
    }
}
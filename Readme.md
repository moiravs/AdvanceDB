# Stream DB with: Apache kafka and Apache Flink

This is our project for the course INFO-F415. The link of the course can be found here: https://cs.ulb.ac.be/public/teaching/infoh415.

The technology we chose are Stream Databases with Apache Kafka & Apache Flink.
We decided to implement a chat that ban users when they write an "illegal" word.
We implemented three differents chats: one with Apache Kafka, one with Apache Flink and one with Apache Kafka & Apache Flink.

Our powerpoint and our report can be found in the `doc` directory.

## Dataset

we also need a chat.csv which is the dataset used, you can get this dataset on http://vps-efc5205a.vps.ovh.net/content/sentiment140.zip

If the producer kafka has a problem with the dataset, you can use csv_repaired.py in producers folder which will modify the dataset chat.csv to be readable.

# Running the code

The installation script is for Ubuntu-based systems. Other systems will need to adapt the installation script.

## Library installation for coding

Installing library for compatibity with python

```
pip install -r requirements
```

Installation of java:

```
sudo apt update
sudo apt install openjdk-11-jdk
```

## Apache Kafka:

### Installation of kafka:

```
wget https://downloads.apache.org/kafka/3.9.0/kafka_2.12-3.9.0.tgz
tar -xvzf kafka_2.12-3.9.0.tgz
sudo mv kafka_2.12-3.9.0 /opt/kafka
rm kafka_2.12-3.9.0.tgz
sudo mkdir -p /tmp/zookeeper/version-2
sudo chown -R $USER:$USER /tmp/zookeeper
sudo mkdir -p /tmp/kafka-logs
sudo chown -R $USER:$USER /tmp/kafka-logs
```

### Launching Kafka servers

```
/opt/kafka/bin/zookeeper-server-start.sh /opt/kafka/config/zookeeper.properties
/opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/server.properties
```

### Topic creation

Creating a topic which is an platform used to transfer data from producers to consumers.

```
/opt/kafka/bin/kafka-topics.sh --create --topic chat --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

You can also delete a topic and recreate it to clear the data inside

```
/opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --topic chat --delete
```

if the deletion is unsuccessful, you can add the option in the config file of kafka server. in server.properties

```
delete.topic.enable=true
```

### Launching kafka

`make kafka`

## Apache Flink

### Installation

Ubuntu-based systems:

```
wget https://archive.apache.org/dist/flink/flink-1.14.4/flink-1.14.4-bin-scala_2.12.tgz
tar -xzvf flink-1.14.4-bin-scala_2.12.tgz
sudo mv flink-1.14.4 /opt/flink
rm flink-1.14.4-bin-scala_2.12.tgz
```

#### Launching a Flink cluster

Before launching Flink, you can edit the conf in /opt/Flink/conf/flink-conf.yaml and add:

```
metrics.reporters:jmx, prometheus
metrics.reporters.jmx.class: org.apache.flink.metrics.jmx.JMXReporter
metrics.reporters.prometheus.class: org.apache.flink.metrics.prometheus.PrometheusReporter
```

```
/opt/flink/bin/start-cluster.sh
```

#### Launching Flink

`make flink`

# Benchmark

## Kafka

- Kafka topic ingestion rate

```
kafka-producer-perf-test.sh --topic chat --num-records 1000000 --record-size 100 --throughput -1 --producer-props bootstrap.servers=localhost:9092
```

- Kafka topic sending rate

```
kafka-consumer-perf-test.sh --bootstrap-server localhost:9092 --topic chat --messages 1000000 --threads 1 --timeout 10000
```

## Flink

Before benchmark, you need to launch the Flink cluster and go to the web monitor http://localhost:8081/#/overview to view the metrics of the flink

### Flink processing time

Send the job to the web monitor

```
cd consumers/flink
/opt/Flink/bin/flink run -c com.flink.Flink target/flink-java-project-1.0-SNAPSHOT.jar
```

you can use the time used by the job as time it take to process all messages.

## Kafka-Flink

Before benchmark, you need to launch the Flink cluster and go to the web monitor http://localhost:8081/#/overview to view the metrics of the flink
Send the job to the web monitor

```
cd consumers/kafka-flink
/opt/Flink/bin/flink run -c com.kafka_flink.Kafka_Flink target/flink-java-project-1.0-SNAPSHOT.jar
```

then you just need to go inside the job you want to check and click on the job again.
Select the tab metrics and choose the metrics for number of messages processed.

https://github.com/yahoo/streaming-benchmarks

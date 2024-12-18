# Stream DB with: Apache kafka and Apache Flink

Kafka and Flink are a distributed event-streaming platform, it doesn't contains a database inside itself because they are
optimized for real-time data streaming and event processing.
it is used to quickly transfer data from producers to consumers.
If we want to store the data, we will need to implement a database at the consumer end.

## Apache Kafka:

### Arch linux

#### Installation of kafka

```
yay kafka
sudo systemctl start zookeeper
sudo systemctl enable zookeeper
sudo systemctl start kafka
sudo systemctl enable kafka
```

#### Topic creation

Creating a topic which is a platform used to transfer data from producers to consumers.

```
/bin/kafka-topics.sh --create --topic advanceddb --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

#### Library installation for coding

Installing library for compatibity with python

```
pip install kafka-python
pip install kafka-python-ng
```

### Ubuntu

#### Preconfiguration

You first need java.

```
sudo apt update
sudo apt install openjdk-11-jdk
java -version
```

#### Installation of kafka

Then you need to download and set kafka

```
wget https://downloads.apache.org/kafka/3.9.0/kafka_2.12-3.9.0.tgz
tar -xvzf kafka_2.12-3.9.0.tgz
sudo mv kafka_2.12-3.9.0 /opt/kafka
rm kafka_2.12-3.9.0.tgz
```

Create folder for logs for kafka and zookeeper

```
sudo mkdir -p /tmp/zookeeper/version-2
sudo chown -R $USER:$USER /tmp/zookeeper
sudo mkdir -p /tmp/kafka-logs
sudo chown -R $USER:$USER /tmp/kafka-logs
```

#### Launching Kafka

We launch zookeeper then kafka, maybe you will need to launch them in background for being able to launch them at the same time.

```
/opt/kafka/bin/zookeeper-server-start.sh /opt/kafka/config/zookeeper.properties

/opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/server.properties
```

#### Topic creation

Creating a topic which is an platform used to transfer data from producers to consumers.

```
/opt/kafka/bin/kafka-topics.sh --create --topic advanceddb --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

You can also delete a topic and recreate it to clear the data inside

```
/opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --topic advanceddb --delete
```

if the deletion is unsuccessfull, you can add the option in the config file of kafka server. in server.properties

```
delete.topic.enable=true
```

#### Library installation for coding

Installing library for compatibity with python

```
pip install kafka-python
pip install kafka-python-ng
```

## Apache Flink

### Arch Installation :

```
yay apache-flink
# Start the cluster, for me it was this path but change it with your path
/home/moira/.cache/yay/apache-flink/src/flink-1.20.0/bin/start-cluster.sh

# to start a server with port 9999 (not when testing with .csv)
nc -lk 9999

mvn clean
mvn -e -f pom.xml compile exec:java


```

### Ubuntu

#### Preconfiguration

You first need java.

```
sudo apt update
sudo apt install openjdk-11-jdk
java -version
```

you also need ssh server

```
sudo apt install openssh-server -y
```

#### Installing Flink

```
wget https://archive.apache.org/dist/flink/flink-1.14.4/flink-1.14.4-bin-scala_2.12.tgz
tar -xzvf flink-1.14.4-bin-scala_2.12.tgz
sudo mv flink-1.14.4 /opt/Flink
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
/opt/Flink/bin/start-cluster.sh
```
## Application
We have multiple Applications:
 - 1 producers in the folder producers.
 - 3 consumers in the folder consumers.
### Preconfiguration
#### Library and executable
You first need java.

```
sudo apt update
sudo apt install openjdk-11-jdk
java -version
```
Producers are written in python so for library you just need to do:
```
pip install -r requirements.txt
```
Consumers are written in java and controlled with maven so for library:
```
mvn clean install
```
Then we can compile the consumers with their pom file.
```
mvn clean package
```
#### kafka and flink
If the application use flink, you just need to launch the Flink cluster.
if the application use kafka, you need to start kafka and create a kafka topic with the name "chat".
#### Launching app
for a producer, we launch with:
```
python3 producer.py
```
for a consumer, we can launch the application using their pom file.
```
mvn exec:java
```
we also need a chat.csv which is the dataset used, you can get this dataset on http://vps-efc5205a.vps.ovh.net/content/sentiment140.zip
#### Combinaison
You can launch the producer kafka with the consumer Kafka or the consumer Kafka-Flink.
You can launch the consumer kafka-flink alone.
#### Error
If the producer kafka have a probleme with the dataset, you can use csv_repaired.py in producers folder to repair the csv and will output cleaned_chat.csv. Replace chat.csv by this file.
## Benchmark
### Kafka
Before benchmark, you need to start kafka and create a kafka topic empty.
#### Kafka topic ingestion rate
To get the kafka topic ingestion rate, we use a tool given by kafka
```
kafka-producer-perf-test.sh --topic chat --num-records 1000000 --record-size 100 --throughput -1 --producer-props bootstrap.servers=localhost:9092
```
Parameters:\\
   --topic chat : the Kafka topic where the messages will be sent.\\
   --num-records 1000000 : the number of messages to be sent.\\
   --record-size 100 : the size of each message in bytes.\\
   --throughput -1: sends messages as quickly as possible.\\
   --producer-props bootstrap.servers=localhost:9092 : the properties of the producer, including the address of the Kafka server.\\
From this command we get data about records/secm, avg latency, max latency and quantiles.
#### Kafka topic sending rate
To get the kafka topic sending rate, we use a tool given by kafka
```
kafka-consumer-perf-test.sh --bootstrap-server localhost:9092 --topic chat --messages 1000000 --threads 1 --timeout 10000
```
Parameters:\\
 - --bootstrap-server localhost:9092 : the address of the Kafka server.\\
 - --topic chat : the Kafka topic from which messages will be consumed.\\
 - --messages 1000000 : the number of messages to be consumed.\\
 - --threads 1 : the number of consuming threads.\\
 - --timeout 10000 : the timeout in milliseconds before stopping the test if no messages are received.\\
From this command we get multiple data about the test but we will only use nbr of MSG/second .
### Flink
Before benchmark, you need to launch the Flink cluster and go to the web monitor http://localhost:8081/#/overview to view the metrics of the flink
#### Flink processing time
Send the job to the web monitor
```
cd consumers/flink
/opt/Flink/bin/flink run -c com.flink.Flink target/flink-java-project-1.0-SNAPSHOT.jar
```
you can use the time used by the job as time it take to process all messages.
#### Kafka-Flink processing time
you also need to launch kafka and the topic "chat" empty.
Send the job to the web monitor
```
cd consumers/kafka-flink
/opt/Flink/bin/flink run -c com.kafka_flink.Kafka_Flink target/flink-java-project-1.0-SNAPSHOT.jar
```
then you just need to go inside the job you want to check and click on the job again.
Select the tab metrics and choose the metrics for number of messages processed.

https://github.com/yahoo/streaming-benchmarks

https://catalog.data.gov/dataset/development-credit-authority-dca-data-set-loan-transactions-a8dbe

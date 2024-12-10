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

## Create the database

kafka and Flink are Stream DB doesn't have a database inside itself so we need to choose another program for storing data.
Here we use postgres

### Ubuntu

#### Postgres installation

```
sudo apt install postgresql postgresql-contrib
```

#### creating a database

```
createdb streamdb
```

#### creating a table

```
createdb streamdb
sudo -iu postgres psql -d streamdb -c
"CREATE TABLE messages (
    id SERIAL PRIMARY KEY,
    value TEXT,
    timestamp TIMESTAMP
);"
```

## DataSet to benchmark

<!--
### Ubuntu
#### Preconfiguration

To use the benchmarks yahoo, you will need to install leiningen and leiningen also need java
```
sudo apt update
sudo apt install openjdk-11-jdk
java -version
```
Then you can install and use leiningen. Install curl with apt install if you don't have curl.
```
curl https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein -o lein
chmod +x lein
sudo mv lein /usr/local/bin/
```
Run leiningen to complete the installation and you can use --version to check if the installation is done.
```
lein
lein --version
```
#### Benchmarks Installation
Download the benchmarks
```
git clone https://github.com/yahoo/streaming-benchmarks.git
```
To install all dependencies. Attention it will install each streaming database for which it have a benchmark. Take a very long time.
```
./stream-bench.sh SETUP
```
#### Configuring
In the file located at conf/benchmarkConf.yaml
You have to configure the kafka topic and create the topic if not exists or you can just put a viable topic here.
then you also have to launch kafka server and flink server.
#### running benchmarks
-->

```
cd consumers/flink
/opt/Flink/bin/flink run -c com.flink.Main target/flink-java-project-1.0-SNAPSHOT.jar
```

now you can monitor on http://localhost:8081/#/overview
We can also check on virtualVM

```
sudo apt install visualvm
visualVM
```

Testing Flink on single node

```
./stream-bench.sh FLINT_TEST
```

## Kafka benchmark

Use kafka-producer-perf-test.sh & kafka-consumer-perf-test.sh

1. Create topic:
   kafka-topics.sh --create --topic chat --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

2. Porducer benchmark:
   kafka-producer-perf-test.sh --topic chat --num-records 1000000 --record-size 100 --throughput -1 --producer-props bootstrap.servers=localhost:9092
   Parameters:
   --topic test-topic : the Kafka topic where the messages will be sent.
   --num-records 1000000 : the number of messages to be sent.
   --record-size 100 : the size of each message in bytes.
   --throughput -1: sends messages as quickly as possible.
   --producer-props bootstrap.servers=localhost:9092 : the properties of the producer, including the address of the Kafka server.

3. Consumer benchmark:
   kafka-consumer-perf-test.sh --bootstrap-server localhost:9092 --topic chat --messages 1000000 --threads 1 --timeout 10000
   Parameters:
   --bootstrap-server localhost:9092 : the address of the Kafka server.
   --topic test-topic : the Kafka topic from which messages will be consumed.
   --messages 1000000 : the number of messages to be consumed.
   --threads 1 : the number of consuming threads.
   --timeout 10000 : the timeout in milliseconds before stopping the test if no messages are received.

4. Clean
   kafka-topics.sh --delete --topic test-topic --bootstrap-server localhost:9092

Notes: - use a Python script with the confluent-kafka library to send the data. - Run the benchmarks several times (min 6 times, ignore the first result).

https://github.com/yahoo/streaming-benchmarks

https://catalog.data.gov/dataset/development-credit-authority-dca-data-set-loan-transactions-a8dbe

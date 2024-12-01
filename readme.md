# Instructions

Students, in groups of four students, will realize a project in a topic relevant to advanced databases. Examples of topics are given in the next section of this document. Please notice that the template for these topics is “<Technology> with <Tool1> and <Tool2>”.

Each group will study a database technology (e.g., document stores, time series databases, etc.) and illustrate it with an application developed ​​in two database management systems to be chosen (e.g., SQL Server, PostgreSQL, MongoDB, etc.). The topic should be addressed in a technical way, to explain the foundations of the underlying technology. The application must use the chosen technology. Examples of technologies and tools can be found for example in the following web site.

It is important to understand that the objective of the project is NOT about developing an application with a GUI. The objective is to benchmark the proposed tool in relation to the database requirements of your application. Therefore, it is necessary to determine the set of queries and updates that your application requires and do a benchmark with, e.g., 1K, 10K, 100K, and 1M “objects” (rows, documents, nodes, etc. depending on the technology used) to determine if the tool shows a linear or exponential behavior. Please notice that you SHOULD NOT generate data for the benchmark since you can find in Internet (1) a huge number of available datasets (2) alternatively, there are many available data generators.

As usual when performing benchmarks, the queries and updates are executed n times (e.g., 6 times where the first execution is not considered because it is different from the others since the cache structures must be filled) and the average of the execution times is computed. A comparison with traditional relational technology (e.g., using PostgreSQL) must be provided to show that the chosen tool is THE technology of choice for your application, better than all other alternatives, and that it will perform correctly when the system is deployed at full scale. Please notice that there are MANY standard benchmarks for various database technologies so in that case you should prefer using a standard benchmark that reinventing the wheel and create your own benchmark.

The choice of topic and the application must be made ​​in agreement with the lecturer. The topic should not be included in the program of the Master in Computer Science and Engineering. The project will be presented to the lecturer and the fellow students at the end of the semester. This presentation will be supported by a slideshow. A written report containing the contents of the presentation is also required. The presentation and the report will (1) explain the foundations of the technology chosen, (2) explain how these foundations are implemented by the database management systems chosen and (3) illustrate all these concepts with the application implemented.

The duration of the presentation is 45 minutes. It will structured in three parts of SIMILAR length

# Stream DB with: Apache kafka et Apache Flink

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

#### topic creation

Creating a topic which is an platform used to transfer data from producters to consumers.

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
wget https://downloads.apache.org/kafka/<version>/kafka_2.13-<version>.tgz
tar -xvzf kafka_2.13-<version>.tgz
sudo mv kafka_2.13-<version> /opt/kafka
```

Create folder for logs for kafka and zookeeper

```
sudo mkdir -p /tmp/zookeeper/version-2
sudo chown -R $USER:$USER /tmp/zookeeper
sudo mkdir -p /tmp/kafka-logs
sudo chown -R $USER:$USER /tmp/kafka-logs
```

#### launching Kafka

We launch zookeeper then kafka, maybe you will need to launch them in background for being able to launch them at the same time.

```
/opt/kafka/bin/zookeeper-server-start.sh /opt/kafka/config/zookeeper.properties

/opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/server.properties
```

#### topic creation

Creating a topic which is an platform used to transfer data from producters to consumers.

```
/opt/kafka/bin/kafka-topics.sh --create --topic advanceddb --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

#### Library installation for coding

Installing library for compatibity with python

```
pip install kafka-python
pip install kafka-python-ng
```

## Apache Flink

### Arch:

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

#### Installing Flink

```
wget https://archive.apache.org/dist/flink/flink-1.18.0/flink-1.18.0-bin-scala_2.12.tgz
tar -xzvf flink-1.18.0-bin-scala_2.12.tgz
sudo mv flink-1.18.0 /opt/Flink
```

#### Launching a Flink cluster

We launch zookeeper then kafka, maybe you will need to launch them in background for being able to launch them at the same time.

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

https://catalog.data.gov/dataset/development-credit-authority-dca-data-set-loan-transactions-a8dbe

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

mvn clean package

# start java (change with your path)
/home/moira/.cache/yay/apache-flink/src/flink-1.20.0/bin/flink run -c com.flink.App target/flink-1.0-SNAPSHOT.jar


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

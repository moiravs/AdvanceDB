# Stream DB with: Apache kafka et Amazon kinesis

## Apache Kafka:

### Arch linux (sorry for other distro idk how to do it)

```
yay kafka
sudo systemctl start zookeeper
sudo systemctl enable zookeeper
sudo systemctl start kafka
sudo systemctl enable kafka
pip install kafka-python
pip install kafka-python-ng
/bin/kafka-topics.sh --create --topic advanceddb --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

#### Create the db

```
createdb streamdb
sudo -u postgres psql
CREATE TABLE messages (
    id SERIAL PRIMARY KEY,
    value TEXT,
    timestamp TIMESTAMP
);
```

### Ubuntu

You first need java.

```
sudo apt update
sudo apt install openjdk-11-jdk
java -version
```

Then you need to download and set kafka

```
wget https://downloads.apache.org/kafka/<version>/kafka_2.13-<version>.tgz
tar -xvzf kafka_2.13-<version>.tgz
mv kafka_2.13-<version> /opt/kafka
```

Create folder for logs for kafka and zookeeper

```
sudo mkdir -p /tmp/zookeeper/version-2
sudo chown -R $USER:$USER /tmp/zookeeper
sudo mkdir -p /tmp/kafka-logs
sudo chown -R $USER:$USER /tmp/kafka-logs
```

We launch zookeeper then kafka, maybe you will need to launch them in background for being able to launch them at the same time.

```
/opt/kafka/bin/zookeeper-server-start.sh /opt/kafka/config/zookeeper.properties

/opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/server.properties
```

## Apache Flink

### Arch:

```
yay apache-flink
# Start the cluster, for me it was this path but change it with your path
/home/moira/.cache/yay/apache-flink/src/flink-1.20.0/bin/start-cluster.sh

# to start a server with port 9999 (not when testing with .csv)
nc -lk 9999

# start java (change with your path)
/home/moira/.cache/yay/apache-flink/src/flink-1.20.0/bin/flink run -c com.flink.App target/flink-1.0-SNAPSHOT.jar


```

## DataSet to benchmark

https://catalog.data.gov/dataset/development-credit-authority-dca-data-set-loan-transactions-a8dbe

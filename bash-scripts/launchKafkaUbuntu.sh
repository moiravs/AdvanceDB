#!/bin/bash

/opt/kafka/bin/zookeeper-server-start.sh /opt/kafka/config/zookeeper.properties > ../zookeper-server.log  2>&1 &

/opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/server.properties > ../kafka-server.log 2>&1  &

/opt/kafka/bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic chat


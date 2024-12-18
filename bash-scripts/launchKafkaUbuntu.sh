#!/bin/bash

/opt/kafka/bin/zookeeper-server-start.sh /opt/kafka/config/zookeeper.properties 2>&1 zookeper-server.log &

/opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/server.properties 2>&1 kafka-server.log &

/opt/kafka/bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic chat


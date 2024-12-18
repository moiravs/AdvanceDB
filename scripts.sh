#!/bin/bash

/opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --topic chat --delete
/opt/kafka/bin/kafka-topics.sh --create --topic chat --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

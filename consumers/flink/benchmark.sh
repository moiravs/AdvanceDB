#!/bin/bash

/opt/Flink/bin/start-cluster.sh
cd ../../
cp chat.csv chat-backup.csv
cd consumers/flink/
for i in {0..9};do
	echo "Iteration $i"
	/opt/Flink/bin/flink run -c com.flink.Main target/flink-java-project-1.0-SNAPSHOT.jar
done
cd ../../
python3 chat-cutter.py 1000000
cd consumers/flink/
for i in {0..9};do
	echo "Iteration $i"
	/opt/Flink/bin/flink run -c com.flink.Main target/flink-java-project-1.0-SNAPSHOT.jar
done
cd ../../
python3 chat-cutter.py 100000
cd consumers/flink/
for i in {0..9};do
	echo "Iteration $i"
	/opt/Flink/bin/flink run -c com.flink.Main target/flink-java-project-1.0-SNAPSHOT.jar
done
cd ../../
python3 chat-cutter.py 10000
cd consumers/flink/
for i in {0..9};do
	echo "Iteration $i"
	/opt/Flink/bin/flink run -c com.flink.Main target/flink-java-project-1.0-SNAPSHOT.jar
done
cd ../../
python3 chat-cutter.py 1000
cd consumers/flink/
for i in {0..9};do
	echo "Iteration $i"
	/opt/Flink/bin/flink run -c com.flink.Main target/flink-java-project-1.0-SNAPSHOT.jar
done
cd ../../
mv chat-backup.csv chat.csv


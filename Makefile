CONSUMER_DIR = consumers
PRODUCER_DIR = producers


kill:
	pkill -f producer.py
	pkill -f flinkproducer.py

kafka:
	python3 $(PRODUCER_DIR)/producer.py &
	java -jar consumers/kafka/target/kafka-java-project-1.0-SNAPSHOT.jar 

flink:
	java -jar consumers/flink/target/flink-java-project-1.0-SNAPSHOT.jar
	
kafka-flink:
	python3 $(PRODUCER_DIR)/producer.py &
	java -jar consumers/kafka-flink/target/flink-java-project-1.0-SNAPSHOT.jar
	

	
	
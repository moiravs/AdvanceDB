#!/bin/bash

separator() {
	echo "$(printf '%.0s-' {1..40})"
}

separator
echo "Project Installer"
separator

separator
echo "Updating..."
separator
sudo apt update

separator
echo "Installing packages..."
separator
sudo apt install openjdk-11-jdk python3-venv libpq-dev

separator
echo "Installing Kafka..."
separator
wget https://downloads.apache.org/kafka/3.9.0/kafka_2.12-3.9.0.tgz
sudo mv kafka_2.12-3.9.0.tgz /opt/kafka
rm kafka_2.12-3.9.0.tgz
sudo mkdir -p /tmp/zookeeper/version-2
sudo chown -R $USER:$USER /tmp/zookeeper
sudo mkdir -p /tmp/kafka-logs
sudo chown -R $USER:$USER /tmp/kafka-logs

separator
echo "Installing Flink..."
separator
wget https://archive.apache.org/dist/flink/flink-1.14.4/flink-1.14.4-bin-scala_2.12.tgz
sudo mv flink-1.14.4-bin-scala_2.12.tgz /opt/flink
rm flink-1.14.4-bin-scala_2.12.tgz

separator
echo "Initalizing python venv..."
separator
python3 -m venv .venv
source .venv/bin/activate
pip install -r ../requirements.txt

separator
echo "Done !"
separator


#!/bin/bash

separator() {
	echo "$(printf '%.0s-' {1..40})"
}

separator
echo "Project Installer"
separator

echo "Are you on :"
echo "1. Ubuntu (apt)"
echo "2. Arch   (pacman)"
echo "3. Other  (manual)"
read -p "Enter your choice: " os

#if choice is neither 1 or 2
if [ "$os" != "1" ] && [ "$os" != "2" ]; then
	echo "You chose manual."
	echo "Please install the following packages :"
	echo "openjdk-11-jdk kafka flink zookeeper python3-venv"
fi

separator
echo "Updating..."
separator

if [ "$os" == "1" ]; then
	sudo apt update
elif [ "$os" == "2" ]; then
	sudo pacman -Syu
fi

separator
echo "Installing packages..."
separator
if [ "$os" == "1" ]; then
	sudo apt install openjdk-11-jdk python3-venv
elif [ "$os" == "2" ]; then
	sudo pacman -S yay
	yay openjdk11 python3-venv
fi

separator
echo "Installing Kafka..."
separator
if [ "$os" == "1" ]; then
	wget https://downloads.apache.org/kafka/3.9.0/kafka_2.12-3.9.0.tgz
	sudo mv kafka_2.12-3.9.0.tgz /opt/kafka
	rm kafka_2.12-3.9.0.tgz
	sudo mkdir -p /tmp/zookeeper/version-2
	sudo chown -R $USER:$USER /tmp/zookeeper
	sudo mkdir -p /tmp/kafka-logs
	sudo chown -R $USER:$USER /tmp/kafka-logs
elif [ "$os" == "2" ]; then
	yay kafka
fi

separator
echo "Installing Flink..."
separator
if [ "$os" == "1" ]; then
	wget https://archive.apache.org/dist/flink/flink-1.14.4/flink-1.14.4-bin-scala_2.12.tgz
	sudo mv flink-1.14.4-bin-scala_2.12.tgz /opt/flink
	rm flink-1.14.4-bin-scala_2.12.tgz
elif [ "$os" == "2" ]; then
	yay apache-flink
fi


separator
echo "Initalizing python venv..."
separator
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt

separator
echo "Done !"
separator


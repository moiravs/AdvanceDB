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
## Amazon Kinesis
Amazon kinesis seem to need to use aws which need to create a account and need to add a billing method so I didn't continue. don't know 
### Ubuntu
We also need Java here, look at Kafka for this.
We need to download and set Aws cli, we will need to install unzip for the installation.

```
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install
```
Then we need to configure aws 

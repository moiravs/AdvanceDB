from kafka import KafkaProducer
from random import randint

producer = KafkaProducer(bootstrap_servers='localhost:9092', value_serializer=lambda v: int(v).to_bytes(4, byteorder='big'))

i = 10
while i:
	randomTemperature = randint(30, 100)
	producer.send('cpu', value=randomTemperature)
	i -= 1


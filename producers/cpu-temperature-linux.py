from kafka import KafkaProducer

producer = KafkaProducer(bootstrap_servers='localhost:9092', value_serializer=lambda v: int(v).to_bytes(4, byteorder='big'))

i = 10
while i:
	with open("/sys/class/thermal/thermal_zone0/temp", "r", encoding="utf-8") as f:
		temperature = int(f.read())
		print(f"{temperature/1000}°C")
		producer.send('cpu', value=temperature)
	i -= 1


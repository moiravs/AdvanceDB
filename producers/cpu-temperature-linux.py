from kafka import KafkaProducer

broker = KafkaProducer(bootstrap_servers='localhost:9092')

i = 10
while i:
	with open("/sys/class/thermal/thermal_zone0/temp", "r", encoding="utf-8") as f:
		temperature = int(f.read())
		print(f"{temperature/1000}°C")
		message = { "temperature": temperature }
		broker.send('advanceddb', value=temperature)
	i -= 1


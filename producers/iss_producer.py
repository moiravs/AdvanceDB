# producer.py
import requests
from kafka import KafkaProducer
import json
import time

# Create a Kafka producer
producer = KafkaProducer(
    bootstrap_servers='localhost:9092',
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

def fetch_iss_data():
    response = requests.get('http://api.open-notify.org/iss-now.json')
    data = response.json()
    return data

def produce_iss_data():
    while True:
        iss_data = fetch_iss_data()
        message = {
            'timestamp': iss_data['timestamp'],
            'latitude': iss_data['iss_position']['latitude'],
            'longitude': iss_data['iss_position']['longitude']
        }
        print(f"Sending: {message}")
        producer.send('advanceddb', value=message)
        time.sleep(10)  # Fetch data every 10 seconds

if __name__ == '__main__':
    produce_iss_data()

# Flush and close the producer
producer.flush()
producer.close()
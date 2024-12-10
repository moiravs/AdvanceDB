# producer.py
from kafka import KafkaProducer
import pandas as pd
import json
import time
def send_message(producer, topic, message):
    try:
        future = producer.send(topic, value=message)
        result = future.get(timeout=10) 
    except Exception as e:
        print(f"Failed to send message: {e}")
        return False
    return True
try:
    producer = KafkaProducer(
        bootstrap_servers='localhost:9092',
        value_serializer=lambda v: json.dumps(v).encode('utf-8')
    )
except:
    print(f"can't join kafka server")
    exit(1)
data = pd.read_csv('../chat.csv')

for index, row in data.iterrows():
    message = {
        'target': row[0],
        'id': row[1],
        'date': row[2],
        'flag': row[3],
        'user': row[4],
        'text': row[5]
    }
    print(f"Sending message: {message}")
    success = send_message(producer, 'chat', message)
    if not success:
        print("Skipping to the next message due to failure.")
    time.sleep(1)

producer.flush()
producer.close()
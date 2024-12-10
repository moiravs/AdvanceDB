# producer.py
from kafka import KafkaProducer
import pandas as pd
import json
import time
def send_message(producer, topic, message):
    try:
        # Try to send the message
        future = producer.send(topic, value=message)
        # Block until a single message is sent (or error occurs)
        result = future.get(timeout=10)  # You can adjust timeout as necessary
        print(f"Message sent: {result}")
    except Exception as e:
        # Handle failure to send message (e.g., broker down)
        print(f"Failed to send message: {e}")
        # Optionally retry, or just pass to next
        return False
    return True
try:
    # Create a Kafka producer
    producer = KafkaProducer(
        bootstrap_servers='localhost:9092',
        value_serializer=lambda v: str(v).encode('utf-8')
    )
except:
    print(f"can't join an kafka server")
    exit(1)
# Load data from the CSV file
data = pd.read_csv('../chat.csv')

# Send data to Kafka topic
for index, row in data.iterrows():
    message = {
        'target': row[0],
        'id': row[1],
        'date': row[2],
        'flag': row[3],
        'user': row[4],
        'text': row[5]
    }
    success = send_message(producer, 'iss', message)
    if not success:
        print("Skipping to the next message due to failure.")
        # Optionally, add a delay before retrying
        time.sleep(1)

# Flush and close the producer
producer.flush()
producer.close()
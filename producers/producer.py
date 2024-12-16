# producer.py
from kafka import KafkaProducer
import pandas as pd
import json
import time

try:
    producer = KafkaProducer(
        bootstrap_servers='localhost:9092',
        value_serializer=lambda v: json.dumps(v).encode('utf-8')
    )
except:
    print(f"can't join kafka server")
    exit(1)
data = pd.read_csv('../cleaned_chat.csv', encoding='utf-8')

for index, row in data.iterrows():
    message = {
        'target': row[0],
        'id': row[1],
        'date': row[2],
        'flag': row[3],
        'user': row[4],
        'text': row[5].encode('latin-1', errors='replace').decode('latin-1')
    }
    # Safely print the message, replacing characters that can't be encoded
    print(f"Sending message: {message}")
    producer.send("chat", value=message)

producer.flush()
producer.close()

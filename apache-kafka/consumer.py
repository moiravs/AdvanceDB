# consumer.py
from kafka import KafkaConsumer
import json
import psycopg2

conn = psycopg2.connect(
    host='localhost',
    database='streamdb',
    user='postgres',
    password='postgres',
    port='5432',
)
cur = conn.cursor()


consumer = KafkaConsumer(
    'advanceddb',
    bootstrap_servers='localhost:9092',
    auto_offset_reset='earliest',
    enable_auto_commit=True,
    group_id='my-group',
    value_deserializer=lambda v: json.loads(v.decode('utf-8'))
)

for message in consumer:
    cur.execute("INSERT INTO messages (value) VALUES (%s)", (message.value['number'],))
    conn.commit()
    print(f"Received: {message.value}")
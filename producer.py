# producer.py
from kafka import KafkaProducer
import pandas as pd
import json
import time

# Create a Kafka producer
producer = KafkaProducer(
    bootstrap_servers='localhost:9092',
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

# Load data from the CSV file
data = pd.read_csv('data/loan_transactions.csv')

# Send data to Kafka topic
for index, row in data.iterrows():
    message = {
        'Guarantee Number': row['Guarantee Number'],
        'Transaction Report ID': row['Transaction Report ID'],
        'Guarantee Country Name': row['Guarantee Country Name'],
        'Amount (USD)': row['Amount (USD)'],
        'Currency Name': row['Currency Name'],
        'Disbursement Date': row['Disbursement Date'],
        'End Date': row['End Date'],
        'Business Sector': row['Business Sector'],
        'City/Town': row['City/Town'],
        'State/Province/Region Name': row['State/Province/Region Name'],
        'State/Province/Region Code': row['State/Province/Region Code'],
        'State/Province/Region Country Name': row['State/Province/Region Country Name'],
        'Region Name': row['Region Name'],
        'Is Woman Owned?': row['Is Woman Owned?'],
        'Is First Time Borrower?': row['Is First Time Borrower?']
    }
    producer.send('loan_transactions', value=message)
    time.sleep(0.1)  # Simulate real-time data streaming

# Flush and close the producer
producer.flush()
producer.close()
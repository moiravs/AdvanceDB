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
data = pd.read_csv('data/Loan_Transactions.csv')

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
    success = send_message(producer, 'advanceddb', message)

    if not success:
        print("Skipping to the next message due to failure.")
        # Optionally, add a delay before retrying
        time.sleep(1)

# Flush and close the producer
producer.flush()
producer.close()
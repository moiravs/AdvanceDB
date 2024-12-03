import requests
from kafka import KafkaProducer
import json
import time

# Binance API URL
API_URL = 'https://api4.binance.com/api/v3/ticker/24hr'

# Configure Kafka Producer
# The producer will send messages to a Kafka topic
producer = KafkaProducer(
    bootstrap_servers='localhost:9092',  # Kafka broker address
    value_serializer=lambda v: json.dumps(v).encode('utf-8')  # Serialize data as JSON
)

def fetch_market_data():
    """
    Fetch market data from Binance API.
    
    Returns:
        list: A list of market data dictionaries.
    """
    try:
        response = requests.get(API_URL)  # Make a GET request to the Binance API
        response.raise_for_status()  # Raise an exception for HTTP errors
        return response.json()  # Parse and return the JSON response
    except requests.exceptions.RequestException as e:
        print(f"Error fetching market data: {e}")
        return []  # Return an empty list in case of an error

def produce_market_data():
    """
    Fetch market data and send it to a Kafka topic continuously.
    """
    while True:
        # Fetch market data
        market_data = fetch_market_data()
        
        if market_data:
            # Loop through each item in the market data
            for item in market_data:
                # Prepare the message with relevant fields
                message = {
                    'symbol': item['symbol'],  # Trading pair (e.g., ETHBTC)
                    'priceChange': item['priceChange'],  # Price change in absolute terms
                    'priceChangePercent': item['priceChangePercent'],  # Price change percentage
                    'weightedAvgPrice': item['weightedAvgPrice'],  # Weighted average price
                    'prevClosePrice': item['prevClosePrice'],  # Previous close price
                    'lastPrice': item['lastPrice'],  # Last traded price
                    'volume': item['volume'],  # 24-hour traded volume
                    'quoteVolume': item['quoteVolume'],  # Quote volume
                    'openPrice': item['openPrice'],  # Opening price of the last 24 hours
                    'highPrice': item['highPrice'],  # Highest price in the last 24 hours
                    'lowPrice': item['lowPrice'],  # Lowest price in the last 24 hours
                    'openTime': item['openTime'],  # Open time (timestamp)
                    'closeTime': item['closeTime']  # Close time (timestamp)
                }

                # Send the message to the Kafka topic 'crypto_market_data'
                producer.send('advanceddb', value=message)
                print(f"Produced: {message}")  # Print the message for debugging
        
        # Wait for 60 seconds before fetching the next batch of data
        time.sleep(5)

# Entry point of the script
if __name__ == '__main__':
    produce_market_data()
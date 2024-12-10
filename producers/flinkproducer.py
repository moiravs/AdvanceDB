import socket
import pandas as pd
import json
import time

def send_message(client_socket, message):
    try:
        client_socket.sendall(message.encode('utf-8'))
    except Exception as e:
        print(f"Failed to send message: {e}")
        return False
    return True

try:
    client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client_socket.connect(('localhost', 9999))
except Exception as e:
    print(f"Can't connect to socket server: {e}")
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
    message_str = json.dumps(message)
    print(f"Sending message: {message_str}")
    success = send_message(client_socket, message_str)
    if not success:
        print("Skipping to the next message due to failure.")
    time.sleep(1)

client_socket.close()
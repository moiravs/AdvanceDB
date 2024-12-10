import socket
import pandas as pd
import json
import time

def start_server(host='localhost', port=9999):
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.bind((host, port))
    server_socket.listen(1)
    print(f"Server listening on {host}:{port}")

    while True:
        client_socket, addr = server_socket.accept()
        print(f"Connection from {addr}")
        send_data(client_socket)
        client_socket.close()

def send_data(client_socket):
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
        message_str = json.dumps(message) + '\n'
        print(f"Sending message: {message_str}")
        try:
            client_socket.sendall(message_str.encode('utf-8'))
        except Exception as e:
            print(f"Failed to send message: {e}")
            break
        time.sleep(1)  # Simulate real-time data streaming

if __name__ == "__main__":
    start_server()
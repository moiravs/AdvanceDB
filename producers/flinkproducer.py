from flask import Flask, request, jsonify
import pandas as pd
import json
import time
import threading

app = Flask(__name__)

data = pd.read_csv('../chat.csv')

@app.route('/stream', methods=['GET'])
def stream_data():
    def generate():
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
            yield message_str
            time.sleep(1)  # Simulate real-time data streaming
    return app.response_class(generate(), mimetype='application/json')

def start_server():
    app.run(host='localhost', port=5000)

if __name__ == "__main__":
    threading.Thread(target=start_server).start()
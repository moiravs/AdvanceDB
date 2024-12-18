from flask import Flask, Response
import pandas as pd
import json
import time

app = Flask(__name__)

# Load the CSV file
data = pd.read_csv('chat.csv')

@app.route('/', methods=['GET'])
def stream_data():
    def generate():
        for _, row in data.iterrows():
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
            time.sleep(0.1)  # Simulate real-time data streaming
    return Response(generate(), mimetype='application/json')

if __name__ == "__main__":
    app.run(host='localhost', port=5000, threaded=True)


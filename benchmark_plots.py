import matplotlib.pyplot as plt
import numpy as np
from scipy.interpolate import make_interp_spline

# Flink data (ms)
data = {
    "1K": [206, 118, 259, 179, 207, 351, 280, 231, 183, 209],
    "10K": [336, 375, 507, 575, 415, 589, 279, 765, 346, 467],
    "100K": [1000, 1000, 1000, 1000, 922, 1000, 1000, 1000, 1000, 1000],
    "1M": [8000, 8000, 8000, 7000, 8000, 8000, 8000, 8000, 8000, 8000],
    "1.6M": [13000, 12000, 12000, 14000, 12000, 13000, 13000, 13000, 14000, 13000]
}

# Average times
average_times = {key: sum(values) / len(values) for key, values in data.items()}
labels = list(average_times.keys())
averages = list(average_times.values())

# Bar graph
plt.figure(figsize=(10, 6))
plt.bar(labels, averages, color='skyblue')
plt.xlabel("Number of messages processed", fontsize=12)
plt.ylabel("Average Time (ms)", fontsize=12)
plt.title("Total Average Time Taken to Process Every Message by Flink​", fontsize=14)

# Show values on top of bars
for i, avg in enumerate(averages):
    plt.text(i, avg + 10, f"{avg:.2f}", ha='center', fontsize=10)

plt.tight_layout()
plt.show()


# Kafka Producer data
records_sent = [1600000, 1000000, 100000, 10000, 1000]
records_per_sec = [440286.186021, 337952.010814, 97276.264591, 17301.038062, 2028.397566]
latency_avg = [27.90, 28.13, 2.45, 6.53, 11.93]
latency_max = [452.00, 452.00, 465.00, 448.00, 457.00]

# Convert records_sent to logarithmic scale for better visualization
records_sent_log = np.log10(records_sent)

# Create a figure and subplots
fig, ax1 = plt.subplots(figsize=(10, 6))

# Records per Second
color = 'tab:blue'
ax1.set_xlabel('Log10 of Records Sent')
ax1.set_ylabel('Records per Second', color=color)
ax1.plot(records_sent_log, records_per_sec, marker='o', color=color, label='Records/sec')
ax1.tick_params(axis='y', labelcolor=color)
ax1.grid(True, which='both', linestyle='--', linewidth=0.5)

# Latency
ax2 = ax1.twinx()
color = 'tab:red'
ax2.set_ylabel('Latency (ms)', color=color)
ax2.plot(records_sent_log, latency_avg, marker='x', color='tab:orange', label='Avg Latency (ms)')
ax2.plot(records_sent_log, latency_max, marker='^', color=color, label='Max Latency (ms)')
ax2.tick_params(axis='y', labelcolor=color)

fig.legend(loc="upper center", bbox_to_anchor=(0.5, 1.15), ncol=3)
plt.title('Kafka Topic Ingestion Rate Results')

plt.tight_layout()
plt.show()


# Kafka End-to-End Latency Quantiles
data = {
    "1000 Records": {
        "50th": [7, 4, 6, 5, 5, 4, 8, 9, 10, 5],
        "95th": [23, 20, 23, 17, 20, 22, 23, 22, 27, 22],
        "99th": [27, 24, 26, 19, 28, 29, 29, 26, 31, 25],
        "99.9th": [486, 445, 447, 433, 442, 446, 442, 444, 441, 335]
    },
    "10000 Records": {
        "50th": [9, 3, 3, 5, 2, 3, 3, 2, 2, 2],
        "95th": [12, 8, 8, 14, 6, 10, 6, 3, 4, 6],
        "99th": [17, 12, 13, 20, 13, 17, 12, 9, 10, 13],
        "99.9th": [21, 15, 17, 24, 16, 21, 15, 13, 14, 15]
    },
    "100000 Records": {
        "50th": [3, 4, 3, 5, 2, 1, 3, 11, 1, 1],
        "95th": [21, 16, 25, 29, 15, 6, 12, 45, 3, 5],
        "99th": [23, 17, 25, 30, 16, 7, 13, 46, 5, 10],
        "99.9th": [25, 17, 25, 30, 17, 17, 14, 47, 16, 12]
    },
    "1000000 Records": {
        "50th": [6, 35, 15, 55, 12, 63, 57, 101, 92, 25],
        "95th": [18, 48, 24, 68, 26, 84, 121, 214, 149, 44],
        "99th": [19, 51, 25, 69, 34, 94, 126, 222, 154, 47],
        "99.9th": [20, 52, 26, 71, 35, 95, 133, 224, 161, 49]
    },
    "1600000 Records": {
        "50th": [8, 12, 1, 1, 31, 4, 3, 3, 42, 2],
        "95th": [35, 28, 3, 25, 65, 44, 41, 29, 149, 47],
        "99th": [39, 36, 9, 38, 67, 48, 49, 34, 167, 56],
        "99.9th": [41, 36, 12, 41, 68, 49, 50, 35, 172, 58]
    }
}

percentiles = ["50th", "95th", "99th", "99.9th"]

# Average latency for each percentile
def calculate_averages(data):
    averages = {}
    for category, latencies in data.items():
        averages[category] = [np.mean(latencies[p]) for p in percentiles]
    return averages

averages = calculate_averages(data)

# Interpolated smooth curve setup
percentiles_fixed = np.linspace(0, 1, len(percentiles))  # Equal spacing for percentiles

# Plot setup
fig, ax = plt.subplots(figsize=(10, 6))
for label, latencies in averages.items():
    # Spline interpolation for smooth curves
    spline = make_interp_spline(percentiles_fixed, latencies, k=2)
    percentiles_smooth = np.linspace(0, 1, 500)
    latencies_smooth = spline(percentiles_smooth)
    
    ax.plot(percentiles_smooth, latencies_smooth, label=label)

ax.set_title("Kafka End-to-End Latency Quantiles")
ax.set_xlabel("Percentile")
ax.set_ylabel("Average Latency (ms)")
ax.set_xticks(percentiles_fixed)
ax.set_xticklabels(["50.00%", "95.00%", "99.00%", "99.90%"])
ax.legend()
ax.grid(True, linestyle='--', alpha=0.5)

plt.tight_layout()
plt.show()



#Kafka Consumer benchmark
data = {
    "1K": [17543.8596, 18867.9245, 18867.9245, 18518.5185, 17857.1429, 18518.5185, 18518.5185, 18518.5185, 18867.9245, 37037.0370],
    "10K": [237568.1818, 243093.0233, 204960.7843, 248880.9524, 201019.2308, 248880.9524, 232288.8889, 237568.1818, 222404.2553, 213326.5306],
    "100K": [643974.3590, 639872.6115, 797301.5873, 784843.7500, 823442.6230, 803680.0000, 702517.4825, 738676.4706, 791023.6220, 643974.3590],
    "1M": [2179368.1917, 2075373.4440, 2128361.7021, 1923711.5385, 2364846.3357, 2451789.2157, 2123842.8875, 974980.5068, 1976936.7589, 2348192.4883],
    "1.6M": [2618846.1538, 2768365.0519, 2996470.0375, 2531827.5316, 2504092.3318, 2644818.1818, 2568402.8892, 2515904.0881, 2935990.8257, 2782808.6957]
}

# Average times
average_times = {key: sum(values) / len(values) for key, values in data.items()}
labels = list(average_times.keys())
averages = list(average_times.values())

# Bar graph
plt.figure(figsize=(10, 6))
plt.bar(labels, averages, color='skyblue')
plt.xlabel("Number of messages fetched", fontsize=12)
plt.ylabel("Fetch Rate (nMsg/sec)", fontsize=12)
plt.title("Kafka Topic Fetch Rate Efficiency", fontsize=14)

# Show values on top of bars
for i, avg in enumerate(averages):
    plt.text(i, avg + 10, f"{avg:.2f}", ha='center', fontsize=10)

plt.tight_layout()
plt.show()



#  Flink Processing (application with Kafka-Flink)
objects = ['1K', '10K', '100K', '1M', '1.6M']
times = [1, 17, 59, 5 * 60 + 32, 8 * 60 + 51]  # Time in seconds
time_labels = ['< 1s' if t == 1 else f'{t}s' for t in times]  # Replace '1' with '< 1s'

# Bar chart
plt.figure(figsize=(10, 6))
bars = plt.bar(objects, times, color='skyblue')

for bar, label in zip(bars, time_labels):
    height = bar.get_height()
    plt.text(bar.get_x() + bar.get_width() / 2, height, label, ha='center', va='bottom')


plt.title('Total Time to Send and Process the Messages by Kafka-Flink')
plt.xlabel('Number of Messages Processed')
plt.ylabel('Time (seconds)')
plt.ylim(0, max(times) + 30)
plt.grid(axis='y', linestyle='--', alpha=0.7)

# Display the chart
plt.tight_layout()
plt.show()
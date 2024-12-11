import matplotlib.pyplot as plt

# Data in milliseconds
data = {
    "1K": [206, 118, 259, 179, 207, 351, 280, 231, 183, 209],
    "10K": [336, 375, 507, 575, 415, 589, 279, 765, 346, 467],
    "100K": [1000, 1000, 1000, 1000, 922, 1000, 1000, 1000, 1000, 1000],
    "1M": [8000, 8000, 8000, 7000, 8000, 8000, 8000, 8000, 8000, 8000],
    "1.6M": [13000, 12000, 12000, 14000, 12000, 13000, 13000, 13000, 14000, 13000]
}

# Calculate average times
average_times = {key: sum(values) / len(values) for key, values in data.items()}
labels = list(average_times.keys())
averages = list(average_times.values())

# Bar graph
plt.figure(figsize=(10, 6))
plt.bar(labels, averages, color='skyblue')

# Labels and title
plt.xlabel("Number of messages sent", fontsize=12)
plt.ylabel("Average Time (ms)", fontsize=12)
plt.title("Average time taken to send every messages", fontsize=14)

# Show values on top of bars
for i, avg in enumerate(averages):
    plt.text(i, avg + 10, f"{avg:.2f}", ha='center', fontsize=10)

# Show the graph
plt.tight_layout()
plt.show()

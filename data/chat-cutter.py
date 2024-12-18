from sys import argv

with open('chat.csv', 'r') as f:
	lines = f.readlines()

lines = lines[:int(argv[1])]

with open('chat.csv', 'w') as f:
	f.writelines(lines)

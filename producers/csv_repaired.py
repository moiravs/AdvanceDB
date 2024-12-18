lines = []
with open('chat.csv', 'r', encoding='utf-8', errors='replace') as f:
    lines = f.readlines()

# Write the cleaned lines to a new file
with open('../cleaned_chat.csv', 'w', encoding='utf-8') as f:
    f.writelines(lines)

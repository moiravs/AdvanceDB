#!/bin/bash

echo "What is your Linux distro : "
echo "1. Ubuntu/Debian"
echo "2. Arch Linux"
read -p "Enter your os [1-2] : " os

if [ $os -eq 1 ]; then
	./launchKafkaUbuntu.sh
	./launchFlinkUbuntu.sh
elif [ $os -eq 2 ]; then
	./launchKafkaArch.sh
	./launchFlinkArch.sh
else
	echo "Invalid input. Aborting..."
fi


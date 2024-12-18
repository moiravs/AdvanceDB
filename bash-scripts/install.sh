#!/bin/bash

echo "Are you on :"
echo "1. Ubuntu/Debian"
echo "2. Arch"
read -p "Enter your os [1-2]: " os

if [ $os -eq 1 ]; then
	./installationUbuntu.sh
elif [ $os -eq 2 ]; then
	./installationArch.sh
else
	echo "Invalid input. Aborting..."
fi


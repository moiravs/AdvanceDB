#!/bin/bash

separator() {
	echo "$(printf '%.0s-' {1..40})"
}

separator
echo "Project Installer"
separator

separator
echo "Updating..."
separator
sudo pacman -Syu

separator
echo "Installing packages..."
separator
sudo pacman -S yay
yay openjdk11 python3-venv

separator
echo "Installing Kafka..."
separator
yay kafka

separator
echo "Installing Flink..."
separator
yay apache-flink

separator
echo "Initalizing python venv..."
separator
python3 -m venv .venv
source .venv/bin/activate
pip install -r ../requirements.txt

separator
echo "Done !"
separator


#!/bin/bash

#Porneste serviciile MySQL si MongoDB
echo "Pornim serviciile MySQL si MongoDB..."
service mysql start
systemctl start mongod

#Ruleaza scripturile Node.js
echo "Rulam scripturile Node.js..."
nohup node index.js > /app/logs/index.log 2>&1 &
nohup node snmp.js > /app/logs/snmp.log 2>&1 &

echo "Toate serviciile si scripturile sunt pornite!"
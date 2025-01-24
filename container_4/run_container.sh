#!/bin/bash


# porneste snmpd
systemctl start snmpd

echo "Serviciul snmpd a fost pornit."

# ruleaza aplicatia java
nohup java -jar RMI_server_co4.jar > /app/logs/co4-rmi.log 2>&1 &

echo "Aplicatia Java ruleaza in fundal."
#!/bin/bash

# porneste snmpd
systemctl start snmpd

echo "Serviciul snmpd a fost pornit."

# ruleaza aplicatia java
nohup java -jar  RMI_server_co5.jar > /app/logs/co5-rmi.log 2>&1 &


echo "Aplicatia Java ruleaza in fundal."
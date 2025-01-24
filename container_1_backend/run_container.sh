#!/bin/bash

# porneste snmpd
systemctl start snmpd

echo "Serviciul snmpd a fost pornit."

# ruleaza aplicatia java
nohup java -jar  target/backend-app-1.0-SNAPSHOT.jar > /app/logs/co1.log 2>&1 &&

echo "Aplicatia Java ruleaza in fundal."
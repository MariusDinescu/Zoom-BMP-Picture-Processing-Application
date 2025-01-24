#!/bin/bash


# porneste snmpd
systemctl start snmpd

echo "Serviciul snmpd a fost pornit."

# ruleaza aplicatia java
nohup java -jar  target/co3-consumer-0.0.1-SNAPSHOT.jar > /app/logs/co3-consumer.log 2>&1 &


echo "Aplicatia Java ruleaza in fundal."
#!/bin/bash
# Script pentru a porni ActiveMQ și aplicația Java

# Pornim brokerul ActiveMQ
echo "Starting ActiveMQ..."
${ACTIVEMQ_HOME}/bin/activemq start

# Așteptăm puțin pentru ca ActiveMQ să se inițializeze
sleep 5

# Apoi rulăm aplicația Java
echo "Starting Java application..."
java -jar /app/target/jmsbroker-1.0.0.jar

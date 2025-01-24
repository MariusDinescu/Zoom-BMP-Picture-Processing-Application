# imaginea folosita
FROM critoma/linux-u20-dev-security-ism:latest

# variabile mediu 
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# Instalam JDK 17 si snmpd
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
	snmpd \
    && apt-get clean;

# Crează directorul logs
RUN mkdir -p /app/logs

# director aplicatie
WORKDIR /app

# Copiere fisier jar pe container
COPY RMI_server_co4.jar /app/RMI_server_co4.jar
COPY run_container.sh /app/run_container.sh

EXPOSE 1099

CMD ["/bin/bash"]

FROM critoma/linux-u20-dev-security-ism:latest

# variabile java
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
	snmpd \
    && apt-get clean;

RUN mkdir -p /app/logs

WORKDIR /app

COPY RMI_server_co5.jar /app/RMI_server_co5.jar
COPY run_container.sh /app/run_container.sh

# expunere port 1100 ptr server
EXPOSE 1100

CMD ["/bin/bash"]


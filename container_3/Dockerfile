FROM critoma/linux-u20-dev-security-ism:latest

# instalare java 17
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    maven \
    && rm -rf /var/lib/apt/lists/*

# instalare snmpd
RUN apt-get install -y snmpd

# variabile java
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

RUN mkdir -p /app/logs

WORKDIR /app

# copiere resurse aplicatie
COPY co3-consumer/src/ /app/src/
COPY co3-consumer/pom.xml /app/
COPY run_container.sh /app/run_container.sh

# instalare maven
RUN apt-get install -y maven

RUN mvn clean install

# expunere porturi
EXPOSE 8080 1099 1100 61616

CMD ["/bin/bash"]
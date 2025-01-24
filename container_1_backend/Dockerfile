FROM critoma/linux-u20-dev-security-ism:latest

RUN apt-get update && apt-get install -y locales
RUN locale-gen en_US.UTF-8

# instalare jdk
RUN apt-get update && apt-get install -y openjdk-17-jdk

# variabile mediu
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$JAVA_HOME/bin:$PATH

# instalare snmpd
RUN apt-get install -y snmpd

RUN mkdir -p /app/logs

WORKDIR /app

COPY c01-backend/src/ /app/src/
COPY c01-backend/pom.xml /app/
COPY run_container.sh /app/run_container.sh

RUN apt-get install -y maven

# construire aplicatie java
RUN mvn clean install

# expunere port 8081
EXPOSE 8081

CMD ["/bin/bash"]

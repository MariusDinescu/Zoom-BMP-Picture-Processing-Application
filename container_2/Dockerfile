FROM critoma/linux-u20-dev-security-ism:latest

# variabile mediu
ENV ACTIVEMQ_VERSION=5.16.0
ENV ACTIVEMQ_HOME=/opt/activemq

# dependinte
RUN apt-get update && apt-get install -y \
    wget \
    maven \
    openjdk-17-jdk \
    && rm -rf /var/lib/apt/lists/*
	
# instalare snmpd
RUN apt-get install -y snmpd

RUN wget https://archive.apache.org/dist/activemq/${ACTIVEMQ_VERSION}/apache-activemq-${ACTIVEMQ_VERSION}-bin.tar.gz -P /opt \
    && tar xzf /opt/apache-activemq-${ACTIVEMQ_VERSION}-bin.tar.gz -C /opt \
    && ln -s /opt/apache-activemq-${ACTIVEMQ_VERSION} ${ACTIVEMQ_HOME}

ENV PATH="${ACTIVEMQ_HOME}/bin:${PATH}"

WORKDIR /app

COPY jmsbroker/src/ /app/src/
COPY jmsbroker/pom.xml /app/

RUN mvn clean install

# Expunere porturi
EXPOSE 61616 8161

CMD ${ACTIVEMQ_HOME}/bin/activemq start && java -jar /app/target/jmsbroker-1.0.0.jar

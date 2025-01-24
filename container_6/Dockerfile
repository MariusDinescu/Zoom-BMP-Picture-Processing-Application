FROM critoma/linux-u20-dev-security-ism:latest

WORKDIR /app

# Instalare dependențe
RUN apt-get update && apt-get install -y \
    curl \
    gnupg2 \
    libsnmp-dev \
    lsb-release \
    ca-certificates \
    mysql-server \
    mongodb \
    snmpd \
    && curl -fsSL https://deb.nodesource.com/setup_18.x | bash - \
    && apt-get install -y nodejs \
    && npm install net-snmp

RUN mkdir -p /app/logs

# Copiere fișiere
COPY package*.json ./
COPY . .
COPY run_container.sh /app/run_container.sh

# dependinte
RUN npm install

# Config mongodb si schema
RUN service mongodb start && \
    mongo --eval "use snmp_data;"

# configurare
RUN service mysql start && \
    mysql -e "CREATE DATABASE co6_photos;" && \
    mysql -e "CREATE TABLE co6_photos.processed_images ( \
        id INT AUTO_INCREMENT PRIMARY KEY, \
        image_data LONGBLOB NOT NULL, \
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP \
    );" && \
    mysql -e "CREATE USER 'root'@'%' IDENTIFIED BY 'root';" && \
    mysql -e "GRANT ALL PRIVILEGES ON co6_photos.* TO 'root'@'%';" && \
    mysql -e "FLUSH PRIVILEGES;"

# Expunere porturi
EXPOSE 3100
EXPOSE 3306

# Comanda default
CMD ["/bin/bash"]

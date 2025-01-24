Project Description

This project involves creating a system that processes large BMP images using Docker containers. The architecture consists of 6 Docker containers connected through a network bridge for efficient communication. Each container plays a specific role in handling the image zooming and system monitoring tasks.

Frontend (React): The frontend is built with React. It allows users to load a BMP image and specify zoom parameter. The frontend communicates with the backend through a REST API.

Container 1 (C01): This container runs Java (Javalin/Apache Tomcat 10). It handles the initial image processing and sends the image data via REST API to the next container. It also acts as a JMS Client Publisher to send a binary message to the JMS Topic.

Container 2 (C02): This container uses Apache TomEE 10 with an ActiveMQ JMS broker. It receives the image data from C01 and publishes it to a JMS Topic.

Container 3 (C03): C03 is responsible for subscribing to the JMS Topic, where it processes the image data. It acts as an EJB Client MDB and also as a Java RMI client, communicating with RMI Servers (C04 and C05). It divides the image into two parts, processes them, and then reconstructs the image.

Container 4 and 5 (C04, C05): These containers are Apache TomEE 10 with RMI Server objects. They handle the image processing tasks by working with the image parts sent by C03.

Container 6 (C06): C06 runs a Node.js server with both MongoDB and MySQL databases. The container stores the processed BMP image as a BLOB in MySQL and stores system data (RAM, CPU usage) in MongoDB. It also exposes two REST API endpoints: one for accessing the database and another for rendering the processed BMP picture. C06 communicates with C03 and notifies when the image is ready. It also collects system data from the other containers via SNMP and stores this information in MongoDB.

All containers are part of a network bridge, making communication between them easier.


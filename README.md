Zoom BMP Picture Processing Application

This project involves the development of a system that handles zooming in and out of BMP images using a microservices architecture spread across six Docker containers. Each container serves a specific purpose in processing and managing the BMP image and associated system data.

Architecture Overview:

Frontend: The frontend (developed using JavaScript, Angular, Vue.js, or React) allows users to upload BMP images and specify zoom parameters (e.g., +/- %). The frontend communicates with the backend via a REST API.

Container 01 (C01): A Java Javalin or Jakarta EE Servlet REST API, which receives the image and parameters from the frontend. This container also acts as a JMS client, publishing the image as a binary message to a JMS topic.

Container 02 (C02): An Apache TomEE 10 container running a JMS broker that manages the topic and queues for message passing.

Container 03 (C03): A Jakarta EE EJB Message-Driven Bean (MDB) subscribed to the JMS topic. It also functions as a Java RMI client, interfacing with two Java RMI server objects located in containers 04 and 05.

Containers 04 & 05 (C04, C05): These containers each run an Apache TomEE 10 instance with RMI server objects, which work together with container C03 to process the image.

Container 06 (C06): A Node.js REST API container hosting two databases—MongoDB and MySQL. The MongoDB stores SNMP data (e.g., OS name, CPU and RAM usage), and the MySQL database stores the zoomed BMP image as a binary large object (BLOB). The Node.js API exposes two REST endpoints: one for SNMP data and one for rendering the BMP picture.

Once the image has been processed and stored in the MySQL database, the frontend is notified via a REST API/WebSocket call from Container 01, redirecting the user to the Node.js URL to download the processed image.


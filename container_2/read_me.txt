#construire imagine docker

docker build -t co2-broker .

# rulare container 

docker run -it --name co2-broker --network dad_project_2024 -p 8161:8161 -p 61616:61616 -d co2-broker:latest

#conectare la container
docker exec -it co2-broker bash

#pornire aplicatia

./run_container.sh

# verificare log uri in directorul /app/logs pentru functionalitate
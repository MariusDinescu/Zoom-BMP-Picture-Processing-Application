#creare bridge network pentru conectare interna
docker network create dad_prokect_2024

#construire imagine docker

docker build -t co1 .

# rulare container 

docker run -it --name co1 --network dad_project_2024 -p 8081:8081 -d co1:latest

#conectare la container
docker exec -it co2-broker bash

#pornire aplicatia

./run_container.sh

# verificare log uri in directorul /app/logs pentru functionalitate
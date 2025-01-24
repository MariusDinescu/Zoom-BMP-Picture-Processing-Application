#construire imagine docker

docker build -t co3-consumer .

# rulare container 

docker run -it --name co3-consumer --network dad_project_2024 -d co3-consumer:latest

#conectare la container
docker exec -it co3-consumer bash

#pornire aplicatia

./run_container.sh

# verificare log uri in directorul /app/logs pentru functionalitate
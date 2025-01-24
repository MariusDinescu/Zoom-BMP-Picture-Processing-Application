#construire imagine docker

docker build -t co5-rmi .

# rulare container 

docker run -it --name co5-rmi --network dad_project_2024 -p 1100:1100 -d co5-rmi:latest

#conectare la container
docker exec -it co5-rmi bash

#pornire aplicatia

./run_container.sh

# verificare log uri in directorul /app/logs pentru functionalitate
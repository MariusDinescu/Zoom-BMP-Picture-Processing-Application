#construire imagine docker

docker build -t co4-rmi .

# rulare container 

docker run -it --name co4-rmi --network dad_project_2024 -p 1099:1099 -d co4-rmi:latest

#conectare la container
docker exec -it co4-rmi bash

#pornire aplicatia

./run_container.sh

# verificare log uri in directorul /app/logs pentru functionalitate
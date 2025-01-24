#construire imagine docker

docker build -t co6 .

# rulare container 

docker run -it --name co6 --network dad_project_2024 -p 3100:3100 -p 3200:3200 -p 3306:3306 -d co6:latest

#conectare la container
docker exec -it co6 bash

#pornire aplicatia

./run_container.sh

# verificare log uri in directorul /app/logs pentru functionalitate
#!/bin/bash



# check if docker is already installed
if ! command -v docker &> /dev/null; then
    echo "Docker is not installed. Installing..."
    sudo apt-get update
    sudo apt-get install -y apt-transport-https ca-certificates curl gnupg lsb-release
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

    echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

    sudo apt-get update
    sudo apt-get install -y docker-ce docker-ce-cli containerd.io
else
    echo "Docker is already installed."
fi

# docker authorization
sudo usermod -aG docker ubuntu

# start docker service
sudo systemctl enable docker
sudo systemctl start docker

# pull MySQL image if not exists
if [ ! "$(docker images -q mysql:latest)" ]; then
    echo "Pulling MySQL Docker image..."
    sudo docker pull mysql:latest
fi

# create docker network if not exists
if [ ! "$(docker network ls | grep docker-network)" ]; then
    sudo docker network create docker-network
fi

# run MySQL container if not exists
MYSQL_ROOT_PASSWORD=$1
DB_USER_NAME=$2
DB_USER_PASSWORD=$3

if [ ! "$(docker ps -a | grep mysql-container)" ]; then
    echo "Starting MySQL container..."
    sudo docker run -d --name mysql-container --network=docker-network \
    -e MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD \
    mysql:latest

    sleep 30

    # create database
    sudo docker exec mysql-container mysql -u root -p$MYSQL_ROOT_PASSWORD -e "CREATE DATABASE IF NOT EXISTS $PROJECT_NAME;"

    # create user and grant privileges
    sudo docker exec mysql-container mysql -u root -p$MYSQL_ROOT_PASSWORD -e "CREATE USER IF NOT EXISTS '$DB_USER_NAME'@'%' IDENTIFIED BY '$DB_USER_PASSWORD'; GRANT ALL PRIVILEGES ON $PROJECT_NAME.* TO '$DB_USER_NAME'@'%'; FLUSH PRIVILEGES;"
fi

# pull Redis image if not exists
if [ ! "$(docker images -q redis:latest)" ]; then
    echo "Pulling Redis Docker image..."
    sudo docker pull redis:latest
fi

# run Redis container if not exists
REDIS_PASSWORD=$4

if [ ! "$(docker ps -a | grep redis-container)" ]; then
    echo "Starting Redis container..."
    sudo docker run -d --name redis-container --network=docker-network \
    -e REDIS_PASSWORD=$REDIS_PASSWORD \
    redis:latest
fi


# run Elastic container if not exists
if [ ! "$(docker ps -a | grep es-container)" ]; then
    echo "Starting Elasticsearch container..."
    sudo docker pull docker.elastic.co/elasticsearch/elasticsearch:7.15.1

    sudo docker run -d \
        -p 9200:9200 -p 9300:9300 \
        -e "discovery.type=single-node" \
        -e "node.name=single-node" \
        -e "cluster.name=green-jangteo-backend" \
        -e "ELASTIC_PASSWORD=elastic" \
        -e "ES_JAVA_OPTS=-Xms128m -Xmx128m" \
        --name es-container \
        --network=docker-network \
        docker.elastic.co/elasticsearch/elasticsearch:7.15.1

    sudo docker exec -it es-container \
         elasticsearch-plugin install analysis-nori \
         https://github.com/skyer9/elasticsearch-jaso-analyzer/releases/download/v7.15.1/jaso-analyzer-plugin-7.15.1-plugin.zip

    sudo docker stop es-container
    sudo docker start es-container

    es:latest

else
    echo "Elasticsearch container already exists."
fi

echo "Docker setup complete."
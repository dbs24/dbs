#!/bin/bash

# clear all images
sudo docker kill $(docker ps -q)
sudo docker rm $(docker ps -a -q)
sudo docker rmi $(docker images -q)

sudo docker system prune -a --volumes

docker ps

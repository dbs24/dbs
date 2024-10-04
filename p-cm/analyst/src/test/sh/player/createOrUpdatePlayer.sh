#!/bin/bash
## get jwt from auth service for userV1
##=======================================================================================================================
##script require export variable $USER_V1"
##script require export variable $PASSWORD_V1"
##script require export variable $URI"
#clear
#FIN_MANAGER_USER="Login203"
#FIN_MANAGER_PASSWORD="Somepassword201"
##=======================================================================================================================
URI="https://kdg-ubuntu.k11dev.tech"
##URI="https://ulia-dev.k11dev.tech"
##URI="https://clouds-dev.k11dev.tech"
##=======================================================================================================================
#export FIN_MANAGER_USER
#export FIN_MANAGER_PASSWORD
#export URI
##=======================================================================================================================
#SEARCH_STR=sss_services
#ROOT_PROJECT_FOLDER=${PWD%%$SEARCH_STR*}$SEARCH_STR
#BASH_PROPERTIES_PATH="$ROOT_PROJECT_FOLDER/sh/bash.properties"
#source <(sed 's@\(.*\)\.\(.*\)=@\1_\2=@' $BASH_PROPERTIES_PATH)
#JWT_SCRIPT_PATH=$ROOT_PROJECT_FOLDER$PLAYER_JWT_PATH
#echo "JWT_SCRIPT_PATH=$JWT_SCRIPT_PATH"
#source "${JWT_SCRIPT_PATH}"
##=======================================================================================================================
##  if JWT is empty"
#if [[ ${#JWT} == 0 ]]; then
#  exit
#fi

JWT="eyJhbGciOiJIUzI1NiJ9.eyJNQU5BR0VSX0xPR0lOIjoiUk9PVCIsIk1BTkFHRVJfUFJJVklMRUdFUyI6IiIsIkpXVF9LRVkiOiJST09UOzE5Mi4xNjguMi4zODsiLCJUT0tFTl9LSU5EIjoiQUNDRVNTX1RPS0VOIiwic3ViIjoiUk9PVDsxOTIuMTY4LjIuMzg7IiwiaWF0IjoxNjkwNDQ4MjE3LCJleHAiOjIxNDg2OTA2NjI5MTd9.1kLBDiuCg4cYzc3U4cRN2JQhs9RMutGopvwL5eMBJqU"

N=286

OLD_LOGIN=null
#OLD_LOGIN=\"Login$N\"
LOGIN="login$N"
#OLD_EMAIL="email$N@yandex.ru"
EMAIL="email$N@yandex.ru"
#OLD_EMAIL=null
OLD_EMAIL=\"$EMAIL\"

UNIQUE_VALUE=$(cat /dev/urandom | tr -dc 'a-zA-Z' | fold -w 15 | head -n 1)
FIRST_NAME="FirstName $UNIQUE_VALUE"
LAST_NAME="LastName $UNIQUE_VALUE"
PHONE="+375445391008$N"
#PASSWORD=null
PASSWORD=\"Somepassword$N\"
URI_ENDPOINT="/api/player/v1/createOrUpdate"
PORT="8443"
#=======================================================================================================================

curl -X 'POST' \
  "$URI:$PORT$URI_ENDPOINT" \
  -H "Authorization: Bearer $JWT" \
  -H 'accept: application/json' \
  -H 'Content-Type: */*' \
  -d "{
        \"version\": \"string\",
        \"requestBodyDto\": {
          \"oldLogin\": $OLD_LOGIN,
          \"login\": \"$LOGIN\",
          \"oldEmail\": $OLD_EMAIL,
          \"email\": \"$EMAIL\",
          \"phone\": \"$PHONE\",
          \"firstName\": \"$FIRST_NAME\",
          \"lastName\": \"$LAST_NAME\",
          \"password\": $PASSWORD
        }
      }" -vvv

#!/bin/bash
# get jwt from auth service for userV1
#=======================================================================================================================
#script require export variable $PLAYER_LOGIN"
#script require export variable $PLAYER_LOGIN"
#script require export variable $URI"
clear
PLAYER_LOGIN="ROOT"
PLAYER_PASSWORD="ROOT-ADMIN-256"
##=======================================================================================================================
URI="https://kdg-ubuntu.k11dev.tech"
#URI="https://ulia-dev.k11dev.tech"
#URI="https://clouds-dev.k11dev.tech"
URI="https://dev01.k11dev.tech"
#=======================================================================================================================
export PLAYER_LOGIN
export PLAYER_PASSWORD
export URI
#=======================================================================================================================
SEARCH_STR=dbs
ROOT_PROJECT_FOLDER=${PWD%%$SEARCH_STR*}$SEARCH_STR
BASH_PROPERTIES_PATH="$ROOT_PROJECT_FOLDER/sh/bash.properties"

#echo "BASH_PROPERTIES_PATH=${BASH_PROPERTIES_PATH}"

source <(sed 's@\(.*\)\.\(.*\)=@\1_\2=@' $BASH_PROPERTIES_PATH)
JWT_SCRIPT_PATH=$ROOT_PROJECT_FOLDER$PLAYER_JWT_PATH

#echo "JWT_SCRIPT_PATH=$JWT_SCRIPT_PATH"
source "${JWT_SCRIPT_PATH}"
#=======================================================================================================================
#  if JWT is empty"
if [[ ${#JWT} == 0 ]]; then
  exit
fi

#JWT="eyJhbGciOiJIUzI1NiJ9.eyJNQU5BR0VSX0xPR0lOIjoiUk9PVCIsIk1BTkFHRVJfUFJJVklMRUdFUyI6IiIsIkpXVF9LRVkiOiJST09UOzE5Mi4xNjguMi4zODsiLCJUT0tFTl9LSU5EIjoiQUNDRVNTX1RPS0VOIiwic3ViIjoiUk9PVDsxOTIuMTY4LjIuMzg7IiwiaWF0IjoxNjkwNDQ4MjE3LCJleHAiOjIxNDg2OTA2NjI5MTd9.1kLBDiuCg4cYzc3U4cRN2JQhs9RMutGopvwL5eMBJqU"

LOGIN="login374"
NEW_STATUS="BANNED"
#OLD_EMAIL="email$N@yandex.ru"
#OLD_EMAIL=\"$EMAIL\"

URI_ENDPOINT="/api/player/status/v1/update"
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
          \"login\": \"$LOGIN\",
          \"newStatus\": \"$NEW_STATUS\"
        }
      }" -vvv

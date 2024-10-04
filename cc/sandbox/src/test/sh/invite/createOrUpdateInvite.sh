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
URI="https://dev01.k11dev.tech"
#URI="https://ulia-dev.k11dev.tech"
#URI="https://clouds-dev.k11dev.tech"
#=======================================================================================================================
export PLAYER_LOGIN
export PLAYER_PASSWORD
export URI
#=======================================================================================================================
SEARCH_STR=chess24
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

INVITE_CODE=null
INVITE_CODE=\"C8240DD487244F8089A653C08CA6210B317B4213AE1C48AFB9\"
INVITE_PLAYER_LOGIN=\"player11\"

URI_ENDPOINT="/api/invite/v1/createOrUpdate"
PORT="7443"
#=======================================================================================================================

curl -X 'POST' \
  "$URI:$PORT$URI_ENDPOINT" \
  -H "Authorization: Bearer $JWT" \
  -H 'accept: application/json' \
  -H 'Content-Type: */*' \
  -d "{
        \"version\": \"string\",
        \"requestBodyDto\": {
          \"inviteCode\": $INVITE_CODE,
          \"playerLogin\": $INVITE_PLAYER_LOGIN,
          \"gameType\": 0,
          \"validDate\": 0,
          \"requiredRating\": 0,
          \"whiteSide\": true
        }
      }" -vvv

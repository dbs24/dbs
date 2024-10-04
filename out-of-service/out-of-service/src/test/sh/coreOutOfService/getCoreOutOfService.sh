#!/bin/bash
# get jwt from auth service for actor manager
#=======================================================================================================================
#script require export variable $ACTORS_MANAGER_LOGIN"
#script require export variable $ACTORS_MANAGER_PASSWORD"
#script require export variable $URI"
clear
#ACTORS_MANAGER_LOGIN="Login203"
ACTORS_MANAGER_LOGIN="ROOT"
#ACTORS_MANAGER_PASSWORD="Somepassword201"
ACTORS_MANAGER_PASSWORD="ROOT-ADMIN-256"
#=======================================================================================================================
URI="https://kdg-ubuntu.smartsafeschool.com"
#URI="https://ulia-dev.smartsafeschool.com"
#URI="https://clouds-dev.smartsafeschool.com"
#=======================================================================================================================
export ACTORS_MANAGER_LOGIN
export ACTORS_MANAGER_PASSWORD
export URI
#=======================================================================================================================
SEARCH_STR=sss_services
ROOT_PROJECT_FOLDER=${PWD%%$SEARCH_STR*}$SEARCH_STR
BASH_PROPERTIES_PATH="$ROOT_PROJECT_FOLDER/sh/bash.properties"
source <(sed 's@\(.*\)\.\(.*\)=@\1_\2=@' $BASH_PROPERTIES_PATH)
JWT_SCRIPT_PATH=$ROOT_PROJECT_FOLDER$ACTORS_MANAGER_JWT_PATH
echo "JWT_SCRIPT_PATH=$JWT_SCRIPT_PATH"
source "${JWT_SCRIPT_PATH}"
#=======================================================================================================================

#  if JWT is empty"
if [[ ${#JWT} == 0 ]]; then
  exit
fi
#=======================================================================================================================
URI_ENDPOINT="/api/out-of-service/v1/get"
PORT="1447"
#=======================================================================================================================
curl -X 'GET' \
  "$URI:$PORT$URI_ENDPOINT" \
  -H "Authorization: Bearer $JWT" \
  -H 'Accept: application/json' \
  -vvv

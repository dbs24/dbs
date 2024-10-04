#!/bin/bash
# get jwt from auth service
#=======================================================================================================================
#clear

#script require export variable $USER_V1"
#script require export variable $PASSWORD_V1"
#script require export variable $URI"

URI_ENDPOINT="/api/smart-safe-school-v1/login"
PORT="1443"
TMPFILE=~/.$USER_V1.jwt

curl -X 'POST' \
  "$URI:$PORT$URI_ENDPOINT" \
  -H 'Accept: application/json' \
  -H 'Content-Type: */*' \
  -d "{
  \"version\": \"string\",
  \"entityAction\": {},
  \"entityInfo\": {
    \"userLogin\": \"$USER_V1\",
    \"userPass\": \"$PASSWORD_V1\",
    \"appPackage\": \"not defined\",
    \"appVersion\": \"0.0.0\"
  }
}" >$TMPFILE

PARSED_KEY="\"jwt\""
PARSED_EXPR="$PARSED_KEY:\"[^\"]*"
PARSED_VALUE='[^"]*$'
JWT=$(grep -o $PARSED_EXPR $TMPFILE | grep -o $PARSED_VALUE)

if [[ ${#JWT} == 0 ]]; then
  PARSED_KEY="\"code\""
  PARSED_EXPR="$PARSED_KEY:\"[^\"]*"
  PARSED_VALUE='[^"]*$'
  OC_ANSWER=$(grep -o $PARSED_EXPR $TMPFILE | grep -o $PARSED_VALUE)

  PARSED_KEY="\"error\""
  PARSED_EXPR="$PARSED_KEY:\"[^\"]*"
  PARSED_VALUE='[^"]*$'
  ERROR=$(grep -o $PARSED_EXPR $TMPFILE | grep -o $PARSED_VALUE)

  RED_FONT='\033[0;31m'
  echo -e "${RED_FONT}!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
  echo -e "### CANNOT RECEIVE JWT FROM \"$URI:$PORT$URI_ENDPOINT\""
  echo -e "### OC: \"$OC_ANSWER\""
  echo -e "### ERROR:\"$ERROR\""
  echo -e "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"

fi

export JWT

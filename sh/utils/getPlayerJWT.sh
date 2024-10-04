#!/bin/bash
# get jwt from auth service
#=======================================================================================================================
#clear

#script require export variable $PLAYER_LOGIN"
#script require export variable $PLAYER_PASSWORD"
#script require export variable $URI"

URI_ENDPOINT="/api/player/v1/login"
PORT="1443"
TMPFILE=~/.$PLAYER_LOGIN.jwt

curl -X 'POST' \
  "$URI:$PORT$URI_ENDPOINT" \
  -H 'Accept: application/json' \
  -H 'Content-Type: */*' \
  -d "{
  \"version\": \"string\",
  \"entityAction\": {},
  \"requestBodyDto\": {
    \"login\": \"$PLAYER_LOGIN\",
    \"password\": \"$PLAYER_PASSWORD\"
  }
}" >$TMPFILE

# check jwt

PARSED_KEY="\"accessJwt\""
PARSED_EXPR="$PARSED_KEY:\"[^\"]*"
PARSED_VALUE='[^"]*$'
JWT=$(grep -o $PARSED_EXPR $TMPFILE | grep -o $PARSED_VALUE)

if [[ ${#JWT} == 0 ]]; then
  PARSED_KEY="\"responseCode\""
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

rm $TMPFILE

export JWT

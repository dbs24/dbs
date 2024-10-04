USER="user"
PASSWORD="123"
URI_ENDPOINT="/api/player/v1/login"
#URI="https://kdg-ubuntu.k11dev.tech"
#URI="https://ulia-dev.k11dev.tech"
URI="https://clouds-dev.k11dev.tech"
PORT="1443"
#=======================================================================================================================
curl -X 'POST' \
  "$URI:$PORT$URI_ENDPOINT" \
  -H 'Accept: application/json' \
  -H 'Content-Type: */*' \
  -d "{
  \"version\": \"string\",
  \"entityAction\": {},
  \"requestBodyDto\": {
    \"login\": \"$USER\",
    \"password\": \"$PASSWORD\"
  }
}" -vvv

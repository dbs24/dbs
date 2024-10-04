JWT="eyJhbGciOiJIUzI1NiJ9.eyJNQU5BR0VSX0xPR0lOIjoiUk9PVCIsIk1BTkFHRVJfUFJJVklMRUdFUyI6IiIsIkpXVF9LRVkiOiJST09UOzE5Mi4xNjguMi4zODsiLCJUT0tFTl9LSU5EIjoiQUNDRVNTX1RPS0VOIiwic3ViIjoiUk9PVDsxOTIuMTY4LjIuMzg7IiwiaWF0IjoxNjkwNDQ4MjE3LCJleHAiOjIxNDg2OTA2NjI5MTd9.1kLBDiuCg4cYzc3U4cRN2JQhs9RMutGopvwL5eMBJqU"
URI_ENDPOINT="/api/player/credentials/v1/get"
MANAGER_LOGIN="ROOT1999999"
URI="https://kdg-ubuntu.k11dev.tech"
#URI="https://ulia-dev.k11dev.tech"
PORT="8443"
#=======================================================================================================================
curl -X 'GET' \
  "$URI:$PORT$URI_ENDPOINT?playerLogin=$MANAGER_LOGIN" \
  -H "Authorization: Bearer $JWT" \
  -H 'accept: application/json' \
  -vvv

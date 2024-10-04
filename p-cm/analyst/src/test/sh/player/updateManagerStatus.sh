JWT="eyJhbGciOiJIUzI1NiJ9.eyJNQU5BR0VSX0xPR0lOIjoiUk9PVCIsIk1BTkFHRVJfUFJJVklMRUdFUyI6IiIsIkpXVF9LRVkiOiJST09UOzE5Mi4xNjguMi4zODsiLCJUT0tFTl9LSU5EIjoiQUNDRVNTX1RPS0VOIiwic3ViIjoiUk9PVDsxOTIuMTY4LjIuMzg7IiwiaWF0IjoxNjkwNDQ4MjE3LCJleHAiOjIxNDg2OTA2NjI5MTd9.1kLBDiuCg4cYzc3U4cRN2JQhs9RMutGopvwL5eMBJqU"
LOGIN="Login203"
STATUS="ACTUAL"
URI_ENDPOINT="/api/manager/status/v1/update"
URI="https://kdg-ubuntu.k11dev.tech"
#URI="https://clouds-dev.k11dev.tech"
#URI="https://ulia-dev.k11dev.tech"
PORT="6443"
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
          \"newStatus\": \"$STATUS\"
        }
      }" -vvv

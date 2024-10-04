JWT_EXPIRED="eyJhbGciOiJIUzI1NiJ9.eyJQTEFZRVJfTE9HSU4iOiJST09UIiwiUExBWUVSX1BSSVZJTEVHRVMiOiIiLCJKV1RfS0VZIjoiUk9PVDsxOTIuMTY4LjIuMzg6MzMwOTY7IiwiSVBfQUREUkVTUyI6IjE5Mi4xNjguMi4zOCIsIlRPS0VOX0tJTkQiOiJBQ0NFU1NfVE9LRU4iLCJzdWIiOiJvcmcuZGJzLmF1dGguc2VydmVyLmNsaWVudHMucGxheWVycyIsImlhdCI6MTcwNzc1NDY1MCwiZXhwIjoxNzA3NzU1MTUwfQ.R-cGe3fIkc57JZSqea1bpsApR49y2byc0xBqyDvoGWk"
JWT_REFRESH="eyJhbGciOiJIUzI1NiJ9.eyJQTEFZRVJfTE9HSU4iOiJST09UIiwiSVBfQUREUkVTUyI6IjE5Mi4xNjguMi4zOCIsIlRPS0VOX0tJTkQiOiJSRUZSRVNIX1RPS0VOIiwic3ViIjoib3JnLmRicy5hdXRoLnNlcnZlci5jbGllbnRzLnBsYXllcnMiLCJpYXQiOjE3MDc3NTQ2NTAsImV4cCI6MTcwNzc1ODI1MH0.ceVY3yrZBy1wJJP1Xgg_MgGzESUu0SdrfCUyk0v5V_g"
URI_ENDPOINT="/api/player/v1/jwt/refresh"
#URI="https://clouds-dev.k11dev.tech"
URI="https://kdg-ubuntu.k11dev.tech"
PORT="1443"
#=======================================================================================================================

curl -X 'POST' \
  "$URI:$PORT$URI_ENDPOINT" \
  -H "Authorization: Bearer $JWT_REFRESH" \
  -H 'Accept: application/json' \
  -H 'Content-Type: */*' \
  -d "{
  \"version\": \"string\",
  \"entityAction\": {},
  \"requestBodyDto\": {
    \"expiredJwt\": \"$JWT_EXPIRED\",
    \"refreshJwt\": \"$JWT_REFRESH\"
  }
}" -vvv

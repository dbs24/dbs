CN_NAME=clouds-dev
CN_HOST="host=ssh://user@clouds-dev.k11dev.tech"

docker context rm  $CN_NAME

docker context create \
    --docker "$CN_HOST" \
    $CN_NAME

docker context ls

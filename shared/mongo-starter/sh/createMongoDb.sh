#!/bin/sh
clear
MONGO_ROOT=root
MONGO_ROOT_PWD=321321
DB_HOST=dbs-dev.k11dev.tech
CRLF='\n'
#================================================================
MONGO_DB=dev_lms
#================================================================
MONGO_USER="${MONGO_DB}_admin"
MONGO_USER_PWD=$(cat /proc/sys/kernel/random/uuid | sed 's/[-]//g' | head -c 25) 2>&1
JS_FILENAME="createMongoDataBase_${MONGO_DB}.js"
MONGO_DB_URL="mongodb://${MONGO_USER}:${MONGO_USER_PWD}@$DB_HOST:27017/${MONGO_DB}?tls=false"

SHELL_CMD="use admin ${CRLF}
           use ${MONGO_DB}${CRLF}
           db.createUser(
           {
            user: \"${MONGO_USER}\",
            pwd:  \"${MONGO_USER_PWD}\",
            roles:
            [
            { role:\"readWrite\",db:\"${MONGO_DB}\"}
            ],
            mechanisms:[\"SCRAM-SHA-1\"]})${CRLF}
            db.system.users.find()"

echo $SHELL_CMD > ${JS_FILENAME}

mongo -u ${MONGO_ROOT} -p ${MONGO_ROOT_PWD} --host ${DB_HOST} --authenticationDatabase admin < ${JS_FILENAME} || exit 1

echo "===================================================="
echo $MONGO_DB_URL
echo "===================================================="

#mongo -u ${MONGO_ROOT} ${MONGO_ROOT_PWD} --host ${DB_HOST} --authenticationDatabase admin < list.js

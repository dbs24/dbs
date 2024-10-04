#!/bin/bash

PASSWD_IN=123123

ALIAS=k11dev
DOMAIN=$ALIAS.tech
JKS_FILE=$DOMAIN.jks
PEM_FILE=$DOMAIN.pem
KEY_FILE=$DOMAIN.key
CRT_FILE=$DOMAIN.crt
P12_FILE=$DOMAIN.p12

clear

rm $JKS_FILE

echo "===openssl ($PASSWD_IN)"

sudo openssl pkcs12 \
        -name $ALIAS \
	-passin pass:"$PASSWD_IN" \
	-passout pass:"$PASSWD_IN" \
	-export \
	-inkey "$KEY_FILE" \
        -certfile "$CRT_FILE" \
        -out "$P12_FILE" \
        -in "$PEM_FILE" 


echo "===keytool jks"


sudo keytool -importkeystore \
	-keystore $ALIAS \
        -srcstorepass $PASSWD_IN \
	-srckeystore $P12_FILE  \
        -srcstoretype pkcs12 \
        -destkeystore $JKS_FILE \
        -deststoretype JKS \
	-deststorepass $PASSWD_IN \
	-destkeypass $PASSWD_IN \
	-noprompt

rm $P12_FILE

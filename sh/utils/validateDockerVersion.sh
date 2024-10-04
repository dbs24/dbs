#!/bin/bash
# validate DOCKER version
#=======================================================================================================================
#clear

DOCKER_VERSION_IS_VALID=1
# validate DOCKER version
SEARCH_STR=docker-compose
ROOT_PROJECT_FOLDER=${PWD%%$SEARCH_STR*}

BASH_PROPERTIES_PATH="$ROOT_PROJECT_FOLDER/sh/bash.properties"
source <(sed 's@\(.*\)\.\(.*\)=@\1_\2=@' $BASH_PROPERTIES_PATH)
EXPECTED_DOCKER_VERSION=$DOCKER_VERSION
EXPECTED_DOCKER_EXPR="${DOCKER_APP_NAME} ${EXPECTED_DOCKER_VERSION}"
GREP_EXPR_RESULT=$(docker -v | grep -o "$EXPECTED_DOCKER_EXPR")

# check grep expression
if [[ ${#GREP_EXPR_RESULT} == 0 ]]; then
  echo "################################################################"
  echo "### expected docker version: $EXPECTED_DOCKER_VERSION"
  PARSED_DOCKER_VERSION="[0-9]{1,2}\.[0-9]{1,2}\.[0-9]{1,2}"
  ACTUAL_DOCKER_VERSION=$(docker -v | grep "${DOCKER_APP_NAME}" | grep -Eo "$PARSED_DOCKER_VERSION")
  echo "### actual docker version: ${ACTUAL_DOCKER_VERSION}"
  echo "### cannot continue"
  echo "################################################################"
  DOCKER_VERSION_IS_VALID=0
else
  echo "################################################################"
  echo "docker version: $GREP_EXPR_RESULT"
  echo "################################################################"
fi

export DOCKER_VERSION_IS_VALID

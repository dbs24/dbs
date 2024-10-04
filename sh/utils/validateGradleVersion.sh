#!/bin/bash
# validate gradle version
#=======================================================================================================================
#clear

GRADLE_VERSION_IS_VALID=1
# validate gradle version
SEARCH_STR=docker-compose
ROOT_PROJECT_FOLDER=${PWD%%$SEARCH_STR*}

BASH_PROPERTIES_PATH="$ROOT_PROJECT_FOLDER/sh/bash.properties"
source <(sed 's@\(.*\)\.\(.*\)=@\1_\2=@' $BASH_PROPERTIES_PATH)
EXPECTED_GRADLE_VERSION=$GRADLE_VERSION
EXPECTED_GRADLE_EXPR="${GRADLE_APP_NAME} ${EXPECTED_GRADLE_VERSION}"
GREP_EXPR_RESULT=$(gradle -v | grep -o "$EXPECTED_GRADLE_EXPR")

# check grep expression
if [[ ${#GREP_EXPR_RESULT} == 0 ]]; then
  echo "################################################################"
  echo "### expected gradle version: $EXPECTED_GRADLE_VERSION"
  PARSED_GRADLE_VERSION='[^ ]*$'
  ACTUAL_GRADLE_VERSION=$(gradle -v | grep "${GRADLE_APP_NAME}" | grep -o "$PARSED_GRADLE_VERSION")
  echo "### actual gradle version: ${ACTUAL_GRADLE_VERSION}"
  echo "### cannot continue"
  echo "################################################################"
  GRADLE_VERSION_IS_VALID=0
else
  echo "################################################################"
  echo "gradle version: $GREP_EXPR_RESULT"
  echo "################################################################"
fi

export GRADLE_VERSION_IS_VALID

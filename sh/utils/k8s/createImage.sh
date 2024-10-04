#!/bin/bash
# get jwt from auth service
#=======================================================================================================================
#clear

#script require export variable $GIT_REPO_NAME
#script require export variable $PROJECT_NAME
#script require export variable $IMAGE_PREFIX
#script require export variable $TAG_PREFIX

SEARCH_STR=sss_services
ROOT_PROJECT_FOLDER=${PWD%%$SEARCH_STR*}$SEARCH_STR
TAG_NAME=$GIT_REPO_NAME/smartsafeschool/backend/sss_services/sss_services/$PROJECT_NAME:$IMAGE_PREFIX-$TAG_PREFIX
PROJECT_FOLDER=$ROOT_PROJECT_FOLDER/$PROJECT_NAME
cd $PROJECT_FOLDER

# only 2 levels deep allowed
if [[ ${PROJECT_NAME} == '3s-banking/stripe/stripe-hooks' ]]; then
  TAG_NAME=$GIT_REPO_NAME/smartsafeschool/backend/sss_services/sss_services/3s-banking/stripe-hooks:$IMAGE_PREFIX-$TAG_PREFIX
fi

echo "//////////////////////////////////////////////////////////////////////////////////////////////////////////////////"
echo "//////////////////////////////////////////////////////////////////////////////////////////////////////////////////"
echo "process '$PROJECT_NAME' ($TAG_NAME)"
echo "================================================================================================================="
sudo docker build -f $PROJECT_FOLDER/Dockerfile -t $TAG_NAME . || exit 1
echo "================================================================================================================="
echo "try 2 push image '$TAG_NAME'"
echo "================================================================================================================="
sudo docker push $TAG_NAME || exit 1

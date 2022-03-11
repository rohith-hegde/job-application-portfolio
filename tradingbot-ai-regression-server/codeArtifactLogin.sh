#!/bin/bash

aws codeartifact login --tool pip --repository CAR_TradingBot-Python-Private_DEV --domain coinfarmtradingbot-priv --domain-owner [censored]
aws codeartifact login --tool twine --repository CAR_TradingBot-Python-Private_DEV --domain coinfarmtradingbot-priv --domain-owner [censored]

CODEARTIFACT_REPOSITORY_NAME=CAR_TradingBot-Python-Private_DEV

export CODEARTIFACT_AUTH_TOKEN=`aws codeartifact get-authorization-token --domain coinfarmtradingbot-priv --domain-owner [censored] --query authorizationToken --output text`
CODEARTIFACT_REPOSITORY_HOSTNAME=coinfarmtradingbot-priv-[censored]9.d.codeartifact.us-east-2.amazonaws.com

# CODEARTIFACT_REPOSITORY_URL=`aws codeartifact get-repository-endpoint --domain coinfarmtradingbot-priv --domain-owner [censored] --repository ${CODEARTIFACT_REPOSITORY_NAME} --format pypi --query repositoryEndpoint --output text`
# poetry config repositories.$CODEARTIFACT_REPOSITORY_NAME $CODEARTIFACT_REPOSITORY_URL
# poetry config http-basic.$CODEARTIFACT_REPOSITORY_NAME aws $CODEARTIFACT_AUTH_TOKEN

POETRY_COMPLETE_CODEARTIFACT_URL=https://aws:${CODEARTIFACT_AUTH_TOKEN}@${CODEARTIFACT_REPOSITORY_HOSTNAME}/pypi/${CODEARTIFACT_REPOSITORY_NAME}/simple

sed "10 c url = \"${POETRY_COMPLETE_CODEARTIFACT_URL}\"" pyproject.toml > pyproject.toml_new
rm pyproject.toml
mv pyproject.toml_new pyproject.toml

# echo $POETRY_COMPLETE_CODEARTIFACT_URL
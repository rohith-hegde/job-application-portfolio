from typing import Any, Dict
from fastapi import FastAPI
from fastapi.openapi.utils import get_openapi
from tradingbot_py_common_classes.src.api.OpenAPI import addOpenApiAwsApiGwExtensions

APIDOC_terms_of_service: str = "Proprietary software. NOT TO BE DISTRIBUTED. Copyright (C) 2021 Coinfarm Ventures L.L.C."

APIDOC_title: str = "Coinfarm trading bot | AI regression"
APIDOC_description: str = f"An API for predicting the market outcomes of CandleFrame/CandleFrameMatrix input with the ai-regression models<br/><br/>{APIDOC_terms_of_service}"
APIDOC_version: str = "0.4.0"
APIDOC_contact: Dict[str, str] = {
    "Coinfarm Ventures L.L.C.": "[censored]"}

AWS_APIGW_ID: str = "[censored]"
VPCL_PROXY_API_PATH: str = "ai-regression"


def custom_openapi(webapp: FastAPI):
    if webapp.openapi_schema:
        return webapp.openapi_schema

    openapi_schema: Dict[str, Any] = get_openapi(
        routes=webapp.routes,
        title=APIDOC_title,
        version=APIDOC_version,
        description=APIDOC_description,
        contact=APIDOC_contact,
    )

    addOpenApiAwsApiGwExtensions(
        openapi_schema=openapi_schema, apigwId=AWS_APIGW_ID,
        apiPath=VPCL_PROXY_API_PATH, awsApiGwPolicy=None)

    webapp.openapi_schema = openapi_schema
    return webapp.openapi_schema

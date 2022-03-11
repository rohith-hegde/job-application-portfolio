
#!/bin/bash

if [ -z "$1" ]; then
    echo "Please specify the version of the microservice. Format: ###.###.####"
	exit 1
fi

API_VERSION=$1
API_NAME="aiRegression"

FILENAME=${API_NAME}-${API_VERSION}_OpenAPISchema-OAS3.0_AutoGenerated.json

curl http://localhost:4006/openapi.json | python -mjson.tool > openapi/$FILENAME

echo
echo "Document saved. File name: openapi/${FILENAME}."

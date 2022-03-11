PKG_NAME="tradingbot-py-common-classes"
pip install --upgrade $PKG_NAME

NEW_PKG_VERSION=`pip show ${PKG_NAME} | grep "Version" | cut -d\  -f2` 
PKG_LINE_NUMBER=`cat pyproject.toml | grep -n "${PKG_NAME}" | cut -d\: -f1`

if [ -z "$PKG_LINE_NUMBER" ]; then
	echo "Error: Dependency '${PKG_NAME}' was not found in 'pyproject.toml' file."
else
	sed "${PKG_LINE_NUMBER} c ${PKG_NAME} = \"^${NEW_PKG_VERSION}\"" pyproject.toml > pyproject.toml_new
	rm pyproject.toml
	mv pyproject.toml_new pyproject.toml
	poetry update "${PKG_NAME}"
	echo "Successfully upgraded package '${PKG_NAME}' to version ${NEW_PKG_VERSION}."
fi

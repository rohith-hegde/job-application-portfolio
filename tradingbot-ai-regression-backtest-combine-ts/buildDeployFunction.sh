currentVersion=$(cat version.txt)
funcName="LMF_ai-regression-backtest-combine-ts_38_DEV"
fileName="${currentVersion}_${funcName}.zip"

powershell -c compress-archive -Path "src/*" -DestinationPath "${fileName}"

aws lambda update-function-code --function-name "${funcName}" --zip-file "fileb://${fileName}" --publish

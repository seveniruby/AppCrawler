#!/usr/bin/env bash
DIR=$(cd `dirname $0` && pwd)
appium --session-override -p 4723 2>&1 | tee appium_ios.log &
sleep 5
bash $DIR/appcrawler -c $DIR/../conf/xueqiu.json -p ios
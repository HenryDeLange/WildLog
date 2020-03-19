#!/bin/bash
cd "$(dirname "$0")"
java -Xms256m -Xmx1024m -jar ./WildLog.jar properties=./wildlog_mac.properties
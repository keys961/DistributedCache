#!/bin/bash

echo "Building cache-client..." && \
cd ./cache-client && mvn install && cd .. && \
echo "cache client build success." && \
echo "Building cache-registry..." && \
cd ./cache-registry && mvn package && cd .. && \
echo "cache-registry build success." && \
echo "Building cache-route..." && \
cd ./cache-route && mvn package && cd .. && \
echo "cache route build success."
# echo "cache route build success." && \
# echo "Building cache-service..." && \
# cd ./cache-service && mvn package && cd .. && \
# echo "cache-service build success."

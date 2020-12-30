#!/usr/bin/env bash
#quit on first error
set -e

PROJECT_DIR="top-words-counter"
VERSION="0.1"
REMOTE_HOST=#your_server_with_yarn_and_spark_support

sbt -mem 4000 clean assembly
ssh $REMOTE_HOST rm -rf $PROJECT_DIR
ssh $REMOTE_HOST mkdir $PROJECT_DIR

for SUBPROJECT in top-words-counter
do
  scp -r ${SUBPROJECT}/*.sh ${REMOTE_HOST}:~/${PROJECT_DIR} &
  scp -r ${SUBPROJECT}/target/scala-2.10/*.jar ${REMOTE_HOST}:~/${PROJECT_DIR} &
  scp ${SUBPROJECT}/src/main/resources/log4j.properties ${REMOTE_HOST}:~/${PROJECT_DIR} &

done

scp log4j.properties src/main/resources/* $REMOTE_HOST:~/$PROJECT_DIR &

#wait for uploads
wait

ssh $REMOTE_HOST chmod +x $PROJECT_DIR/*.sh
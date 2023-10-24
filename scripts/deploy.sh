#!/bin/bash

# 작업 디렉토리를 /home/ubuntu로 변경
cd /home/ubuntu

# 환경변수 DOCKER_APP_NAME을 jcdoker/docker_test로 설정
DOCKER_APP_NAME=docker
EXIST_BLUE=$(sudo docker-compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml ps  | grep up)
BLUE_HEALTH=$(sudo docker-compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml ps | grep up)
GREEN_HEALTH=$(sudo docker-compose -p ${DOCKER_APP_NAME}-green -f docker-compose.green.yml ps | grep up)

# green이 실행중이면 blue up
# EXIST_BLUE 변수가 비어있는지 확인
if [ -z "$EXIST_BLUE" ]; then
    # docker-compose.blue.yml 파일을 사용하여 docker-blue 프로젝트의 컨테이너를 빌드하고 실행
    sudo docker-compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml up -d --build

  # 30초 동안 대기
  sleep 30

 # blue가 현재 실행중이지 않다면 -> 런타임 에러 또는 다른 이유로 배포가 되지 못한 상태
  if [ -z "$BLUE_HEALTH" ]; then
    # slack으로 알람을 보낼 수 있는 스크립트를 실행한다.
    sudo ./slack_blue.sh
  # blue가 현재 실행되고 있는 경우에만 green을 종료
  else
    # docker-compose.green.yml 파일을 사용하여 docker-green 프로젝트의 컨테이너를 중지
    sudo docker-compose -p ${DOCKER_APP_NAME}-green -f docker-compose.green.yml down

     # 사용하지 않는 이미지 삭제
    sudo docker image prune -af
  fi

# blue가 실행중이면 green up
else
  sudo docker-compose -p ${DOCKER_APP_NAME}-green -f docker-compose.green.yml up -d --build

  sleep 30

  if [ -z "$GREEN_HEALTH" ]; then
      sudo ./slack_green.sh
  else
    sudo docker-compose -p ${DOCKER_APP_NAME}-blue -f docker-compose.blue.yml down
    sudo docker image prune -af
  fi
fi
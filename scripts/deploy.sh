#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/app # 실행 경로 설정

echo "> 현재 구동 중인 애플리케이션 pid 확인"

# 자바로 실행중인 프로세스 PID와 이름 검색 | 해당 이름이 들어간 프로세스 선택 | 해당 프로세스의 PID 선택
CURRENT_PID=$(pgrep -fla java | grep SNAPSHOT | awk '{print $1}')

echo "현재 구동 중인 애플리케이션 pid: $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then # 실행중인 PID가 없다면
  echo "현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다."
else # PID가 있으면 PID를 가진 소프트웨어 강제 종료
  echo "> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> 새 애플리케이션 배포"

JAR_NAME=$(ls -tr $REPOSITORY/*SNAPSHOT.jar | tail -n 1) # 배포한 Jar파일 이름 설정

echo "> $JAR_NAME 에 실행권한 추가"

chmod +x $JAR_NAME # Jar파일 실행 권한 추가

echo "> $JAR_NAME 실행"
# Seoul시간대로 설정하여 자동 실행
nohup java -jar -Duser.timezone=Asia/Seoul $JAR_NAME >> $REPOSITORY/nohup.out 2>&1 &
# Java 17을 사용하는 Amazon Corretto 이미지 선택
FROM amazoncorretto:17

# 작업 디렉토리 생성
WORKDIR /app

# JAR 파일 복사
COPY build/libs/Backend-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]
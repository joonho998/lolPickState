# OpenJDK 기반 이미지 사용
FROM openjdk:8-jdk

# WAR 파일을 컨테이너 내부로 복사
COPY target/my-app.war /app/lolTest.war

# 애플리케이션 실행
CMD ["java", "-jar", "/app/lolTest.war"]
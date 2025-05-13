# 1단계: Maven + Java 8으로 빌드
FROM maven:3.6.3-jdk-8 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 2단계: 실행 전용 컨테이너
FROM openjdk:8-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/lolTest-0.0.1-SNAPSHOT.war /app/lolTest.war

# Render에서 사용할 포트 노출
EXPOSE 8080

# 실제 실행 커맨드
CMD ["java", "-jar", "/app/lolTest.war"]
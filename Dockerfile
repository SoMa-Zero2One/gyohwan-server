# Gradle과 JDK 17을 포함한 이미지를 빌드 환경으로 사용
FROM gradle:jdk17-focal AS builder

# 작업 디렉토리 설정
WORKDIR /build

# 빌드에 필요한 파일들만 먼저 복사하여 캐싱 활용
COPY build.gradle settings.gradle ./
COPY src ./src

# Gradle을 이용해 프로젝트 빌드(테스트 스킵) -> 이 명령이 성공하면 build/libs/ 폴더에 .jar 파일이 생성됨
RUN gradle build --no-daemon -x test

# 실제 애플리케이션을 실행할 환경
FROM openjdk:21

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 환경(builder)에서 생성된 JAR 파일을 실행 환경으로 복사
COPY --from=builder /build/build/libs/*.jar app.jar

# 컨테이너가 시작될 때 이 명령어를 실행하여 애플리케이션 구동
ENTRYPOINT ["java", "-jar", "app.jar"]
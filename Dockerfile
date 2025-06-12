FROM amazoncorretto:17-alpine AS build
WORKDIR /workspace/app

COPY gradlew build.gradle settings.gradle ./
COPY gradle  ./gradle

RUN ./gradlew --no-daemon dependencies

COPY src ./src
RUN ./gradlew --no-daemon clean bootJar -x test

FROM amazoncorretto:17-alpine AS runtime
WORKDIR /app

EXPOSE 80

ENV PROJECT_NAME=deokhugam-team7 \
    PROJECT_VERSION=0.0.1-SNAPSHOT \
    JVM_OPTS=""

COPY --from=build /workspace/app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar ./

# entrypoint 복사
COPY entrypoint.sh /entrypoint.sh

# **CR 제거 + 실행권한**
RUN sed -i 's/\r$//' /entrypoint.sh

RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
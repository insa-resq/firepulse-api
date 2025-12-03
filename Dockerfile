FROM maven:3.9.9-eclipse-temurin-21-alpine AS build-stage

WORKDIR /app

ARG SERVICE_NAME

COPY ${SERVICE_NAME}/pom.xml .

RUN mvn -ntp -B dependency:go-offline

COPY ${SERVICE_NAME}/src ./src

RUN mvn -ntp -B -DskipTests package && \
    cp target/*.jar application.jar && \
    java -Djarmode=tools -jar application.jar extract --layers --launcher --destination extracted &&  \
    rm -rf /root/.m2/repository

FROM eclipse-temurin:21-jre-alpine AS runtime-stage

RUN addgroup -S spring &&  \
    adduser -S spring -G spring

USER spring

WORKDIR /app

COPY --from=build-stage /app/extracted/dependencies/ ./
COPY --from=build-stage /app/extracted/spring-boot-loader/ ./
COPY --from=build-stage /app/extracted/snapshot-dependencies/ ./
COPY --from=build-stage /app/extracted/application/ ./

ENTRYPOINT ["java", "org/springframework/boot/loader/launch/JarLauncher"]

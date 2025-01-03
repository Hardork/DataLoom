# Docker 镜像构建
# @author <a href="https://github.com/Hardork">老山羊</a>
#
FROM maven:3.8.1-jdk-8-slim as builder

# Copy local code to the container image.
WORKDIR /app
COPY pom.xml .
COPY src ./src
EXPOSE 8081

# Build a release artifact.
RUN mvn package -DskipTests

# Run the web service on container startup.
CMD ["java","-jar","/app/target/admin-backend-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]
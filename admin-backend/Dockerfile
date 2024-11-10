# Docker 镜像构建
# @author <a href="https://github.com/Hardork">老山羊</a>
#
FROM maven:3.8.1-jdk-8-slim as builder

# Copy local code to the container image.
WORKDIR /app
COPY --from=builder /app/target/admin-backend-*.jar app.jar
EXPOSE 8081

ENV nacos-server=121.36.48.205

# Run the web service on container startup.
CMD ["java","-jar","/app/target/admin-backend-SNAPSHOT.jar","--spring.profiles.active=prod"]
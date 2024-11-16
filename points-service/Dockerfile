# Docker 镜像构建
# @author <a href="https://github.com/Hardork">老山羊</a>
#
FROM openjdk:8-jre-slim
# 设置工作目录
WORKDIR /app

# 将 Maven 打包生成的 JAR 文件复制到容器中
COPY target/points-service-*.jar app.jar
EXPOSE 8082

ENV nacos-server=121.36.48.205

# 运行应用程序
CMD ["java", "-jar", "app.jar"]
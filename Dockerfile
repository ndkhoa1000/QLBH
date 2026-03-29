# Stage 1: Build with Maven
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
COPY qlbh-common qlbh-common
COPY qlbh-product-service qlbh-product-service
COPY qlbh-customer-service qlbh-customer-service
COPY qlbh-supplier-service qlbh-supplier-service
COPY qlbh-invoice-service qlbh-invoice-service
COPY qlbh-web qlbh-web

# Build all modules
RUN mvn clean package -DskipTests

# Stage 2: Run with Tomcat
FROM tomcat:9.0-jdk17-openjdk-slim
ARG MODULE_NAME
COPY --from=build /app/${MODULE_NAME}/target/*.war /usr/local/tomcat/webapps/QLBH.war
EXPOSE 8080
CMD ["catalina.sh", "run"]

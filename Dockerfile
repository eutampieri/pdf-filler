FROM eclipse-temurin:25
RUN mkdir /opt/app
COPY app/build/libs/app-all.jar /opt/app
CMD ["java", "-jar", "/opt/app/app-all.jar"]
EXPOSE 8080

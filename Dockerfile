FROM eclipse-temurin:17
RUN mkdir /opt/app
COPY app/build/libs/app-all.jar /opt/app/pdf-filler.jar
CMD ["java", "-jar", "/opt/app/pdf-filler.jar"]
EXPOSE 8080

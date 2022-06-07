FROM maven:latest

ENV APP_HOME=/geographic-distance-app/

COPY pom.xml $APP_HOME
COPY . $APP_HOME/
WORKDIR $APP_HOME

RUN mvn package -DskipTests
ENV JAR_FILE=target/geographic-distance-0.0.1-SNAPSHOT.jar

RUN mv ${JAR_FILE} /geographic-distance-app.jar

ENTRYPOINT ["java", "-jar", "/geographic-distance-app.jar"]
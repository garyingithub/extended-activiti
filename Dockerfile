FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/cloud-workflow-1.0-SNAPSHOT.jar app.jar
ADD target/newrelic newrelic
ADD target/newrelic.yml newrelic.yml
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java  -javaagent:/newrelic/newrelic.jar -jar /app.jar" ]
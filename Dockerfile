FROM openjdk:15
ADD build/libs/mefit-0.0.1-SNAPSHOT.jar mefit.jar
ENTRYPOINT ["java", "-jar", "mefit.jar"]
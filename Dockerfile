FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY Calculator.java ./
RUN javac Calculator.java

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/Calculator.class ./
ENTRYPOINT ["java", "Calculator"]
CMD ["add", "1", "1"]

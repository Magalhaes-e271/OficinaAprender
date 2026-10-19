# Compila o backend com Java 21 e o Gradle Wrapper do projeto.
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
COPY src ./src

# Normaliza as quebras de linha do script enviado pelo Windows.
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew
RUN ./gradlew --no-daemon bootJar

# Executa somente o JAR da aplicação.
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/app.jar

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=60.0 -XX:+ExitOnOutOfMemoryError -Djava.awt.headless=true"
USER 10001

EXPOSE 10000

# Usa a porta fornecida pelo Render e aceita conexões externas.
CMD ["sh", "-c", "exec java -Dserver.port=${PORT:-10000} -Dserver.address=0.0.0.0 -jar /app/app.jar"]

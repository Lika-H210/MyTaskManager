# ===== ビルド用ステージ =====
FROM gradle:8.14.3-jdk21 AS build
WORKDIR /app
COPY . .
RUN ./gradlew build -x test

# ===== 実行用ステージ =====
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
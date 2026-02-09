# マルチステージビルド: ビルドステージ
FROM maven:3.9-eclipse-temurin-25-alpine AS build

WORKDIR /app

# 依存関係のキャッシュ最適化のため、pom.xmlを先にコピー
COPY pom.xml .
RUN mvn dependency:go-offline -B

# ソースコードをコピーしてビルド
COPY src ./src
RUN mvn clean package -DskipTests -B

# 実行ステージ
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# タイムゾーン設定（日本時間）
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Tokyo /etc/localtime && \
    echo "Asia/Tokyo" > /etc/timezone && \
    apk del tzdata

# ビルドステージからJARファイルをコピー
COPY --from=build /app/target/*.jar app.jar

# アプリケーション実行用のユーザーを作成（セキュリティ向上）
RUN addgroup -g 1000 spring && \
    adduser -u 1000 -G spring -s /bin/sh -D spring

USER spring:spring

# ヘルスチェック（Spring Boot Actuatorを使用する場合）
# HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
#   CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

# JVMメモリ設定とアプリケーション起動
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}", \
    "-jar", \
    "app.jar"]

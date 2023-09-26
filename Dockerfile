FROM node as frontend
WORKDIR /project
COPY frontend/src ./src
COPY frontend/package.json ./package.json
COPY frontend/package-lock.json ./package-lock.json
COPY frontend/webpack.config.js ./webpack.config.js
RUN npm ci && npm run build

FROM gradle:jdk20 as backend
WORKDIR /project
COPY backend/src ./src
COPY backend/build.gradle.kts ./build.gradle.kts
COPY backend/settings.gradle.kts ./settings.gradle.kts
COPY --from=frontend /project/dist ./src/main/resources/public
RUN gradle clean build

FROM eclipse-temurin as app
WORKDIR /root
RUN apt -y update && apt install -y openssh-client
COPY --from=backend /project/build/libs/*-boot.jar ./app
ENTRYPOINT ["java", "-jar", "--enable-preview", "/root/app"]

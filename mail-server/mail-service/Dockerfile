ARG OS_VERSION=latest
ARG JVM_VERSION=23-jdk-alpine
# ------------------------------
FROM ubuntu:${OS_VERSION}
FROM eclipse-temurin:${JVM_VERSION}
# ------------------------------
LABEL maintainer="info@test.k11dev.tech"
ENV TZ Europe/Moscow
ENV PORT="443"
ENV APP_NAME="mail-service"
ENV APP_JAR="$APP_NAME.jar"
ENV APP_FLD="/opt/$APP_NAME"
ENV LOG_FLD="/var/log/dbs/"
ENV HEAP_DUMP_FLD="${LOG_FLD}heap/dump/"
ENV APP_RESOURCES="src/main/resources"
ENV MAX_OPEN_FILES=1048576
ARG JAR_FILE="build/libs/$APP_JAR"
ARG CONF_FILE=$APP_RESOURCES/*.*
RUN mkdir -p $APP_FLD
RUN mkdir -p $HEAP_DUMP_FLD
EXPOSE ${PORT}
COPY ${JAR_FILE} $APP_FLD/$APP_JAR
COPY ${CONF_FILE} $APP_FLD/

ENTRYPOINT java \
  -Dlog4j.configurationFile=${APP_FLD}/log4j2.xml \
  -XX:+PrintFlagsFinal \
  -XX:HeapDumpPath=${HEAP_DUMP_FLD} \
  -Xms128M \
  -Xmx512M \
  -XX:MetaspaceSize=256M \
  -XX:MaxDirectMemorySize=256M \
  -XX:MaxGCPauseMillis=20 \
  -XX:+ExplicitGCInvokesConcurrent \
#  -Xlog:gc* \
  -Xlog:gc:${LOG_FLD}${APP_NAME}-gc.log:tags,time,uptime,level:filecount=5,filesize=20M \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:+ExitOnOutOfMemoryError \
  -jar ${APP_FLD}/${APP_JAR} \
#  --server.port=${PORT} \
#  --config.security.profile.webfilter.chain=development \

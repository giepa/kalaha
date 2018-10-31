FROM openjdk:8-jre-alpine

ENV SERVICE_HOME /usr/service
ENV EXEC "java \
    -Xmx64m \
    -Dhazelcast.logging.type=slf4j \
    -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory \
    -jar app.jar "

COPY ./service $SERVICE_HOME/

EXPOSE 8080

WORKDIR $SERVICE_HOME

ENTRYPOINT ["sh", "-c"]
CMD ["$EXEC"]
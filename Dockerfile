FROM hseeberger/scala-sbt:8u222_1.3.6_2.12.10 AS builder

COPY ./build.sbt /opt/build.sbt
COPY ./project/plugins.sbt /opt/project/plugins.sbt
COPY ./src /opt/src
RUN cd /opt; sbt clean assembly

FROM flink:1.11.2-scala_2.12
#Download connector libraries
RUN wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-sql-connector-kafka_2.12/1.11.2/flink-connector-kafka_2.12-1.11.2.jar; \
    wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-connector-jdbc_2.12/1.11.2/flink-connector-jdbc_2.12-1.11.2.jar; \
    wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/org/apache/flink/flink-csv/1.11.2/flink-csv-1.11.2.jar; \
    wget -P /opt/flink/lib/ https://repo.maven.apache.org/maven2/mysql/mysql-connector-java/8.0.22/mysql-connector-java-8.0.22.jar;

COPY --from=builder /opt/target/scala-2.12/flink-playgrounds-scala.jar /opt/flink/usrlib/flink-playgrounds-scala.jar

RUN echo "execution.checkpointing.interval: 10s" >> /opt/flink/conf/flink-conf.yaml; \
    echo "pipeline.object-reuse: true" >> /opt/flink/conf/flink-conf.yaml; \
    echo "pipeline.time-characteristic: EventTime" >> /opt/flink/conf/flink-conf.yaml; \
    echo "taskmanager.memory.jvm-metaspace.size: 512m" >> /opt/flink/conf/flink-conf.yaml;

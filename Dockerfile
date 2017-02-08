# To build: docker build -t generalmeow/mysharesconsumer:<TAG> .
# To run: docker run generalmeow/mysharesconsumer:<TAG> --spring.config.name=<ENV> <DBPROFILE> ... ENV application/dev/prod, EBPROFILE=mongodb/rethinkdb/influxdb
# i.e docker run generalmeow/mysharesconsumer:<TAG> --spring.config.name=dev rethinkdb
FROM anapsix/alpine-java:8
MAINTAINER Paul Hoang 2017-01-05
RUN ["mkdir", "-p", "/home/javaapp"]
WORKDIR /home/javaapp
COPY ./build/libs/mySharesConsumer-1.4.jar /home/javaapp/mySharesConsumer.jar
ENTRYPOINT ["java", "-jar", "mySharesConsumer.jar"]
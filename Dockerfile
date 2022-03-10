FROM maven:3.6.3 AS build
WORKDIR /usr/src/app
COPY . /usr/src/app
RUN mvn install

FROM tomcat:8-jre8-openjdk
COPY --from=build /usr/src/app/target/*.war webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh","run"]
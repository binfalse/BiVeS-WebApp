FROM tomcat:8-jre8
MAINTAINER martin scharm

# remove the default tomcat application
RUN rm -rf /usr/local/tomcat/webapps/ROOT /usr/local/tomcat/webapps/ROOT.war

# add the BiVeS-WebApp as the new default web app
COPY BiVeS-WebApp-DockerContext.xml /usr/local/tomcat/conf/Catalina/localhost/ROOT.xml
COPY BiVeS-WebApp-${project.version}.war /usr/local/tomcat/webapps/ROOT.war


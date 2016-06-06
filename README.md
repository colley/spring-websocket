## Overview

Demonstrates Spring WebSocket and SockJS support in Spring Framework 4.0.


### example
your can view https://osdims-jiangchaoxun.rhcloud.com/spring-websocket/index.html

### Tomcat

The app has been tested with this [Tomcat 8 snapshot](https://repository.apache.org/content/repositories/snapshots/org/apache/tomcat/tomcat/8.0-SNAPSHOT/tomcat-8.0-20130815.225136-6.zip). We are also expecting an RC2 release soon as well as a backport to Tomcat 7.

After unzipping Tomcat 8, set `TOMCAT8_HOME` as an environment variable and use [deployTomcat8.sh](https://github.com/colley/spring-websocket/master/deployTomcat8.sh) and [shutdownTomcat8.sh](https://github.com/colley/spring-websocket/master/shutdownTomcat8.sh) in this directory.

Open a browser and go to <http://localhost:8080/spring-websocket/index.html>

### Jetty 9

The easiest way to run on Jetty 9.1.0:

    mvn jetty:run

Open a browser and go to <http://localhost:8080/spring-websocket/index.html>

**Note:** To deploy to a Jetty installation, add this to Jetty's `start.ini`:

    OPTIONS=plus
    etc/jetty-plus.xml
    OPTIONS=annotations
    etc/jetty-annotations.xml

### Glassfish

Glassfish 4 provides JSR-356 support.

Download Glassfish 4 and unzip the downloaded distribution.

Start the server:

    cd <unzip_dir>/glassfish4
    bin/asadmin start-domain

Deploy the WAR file using the script in this directory.

Open a browser and go to <http://localhost:8080/spring-websocket/index.html>

Watch the logs:

    cd <unzip_dir>/glassfish4
    less `glassfish/domains/domain1/logs/server.log`



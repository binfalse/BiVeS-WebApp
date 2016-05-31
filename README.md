# BiVeS-WebApp
This is the BiVeS web application, a JAVA based web interface to the [BiVeS tool] for difference detection between versions of computational models.

## Build
We recommend building the application with Maven.

### Maven
Along with the source code you will obtain a pom.xml that contains the configuration to build the web application with maven. Maven will download and manage all dependencies. Just call a

    mvn package

to obtain a `BiVeS-WebApp-*.war` in the `target` directory. This war file can then be deployed to a JAVA compliant webserver, such as the Apache Tomcat.


### Ant
If you prefer using and you need to download the dependencies yourself. Create a directory `lib` in the project directory to store dependent libraries. The following libraries are required:

* BiVeS (download a fat jar from http://bin.sems.uni-rostock.de/)
* json-simple (see https://code.google.com/p/json-simple/)
* javax.servlet-api (see e.g. http://repo1.maven.org/maven2/javax/servlet/javax.servlet-api/)

Then just call 

    ant dist

to obtain the `war` file that can be deployed to a JAVA compliant webserver, such as the Apache Tomcat.

## Binaries

### Java Binaries
Pre-compiled Java binaries can be found at http://bin.sems.uni-rostock.de/BiVeS-WebApp/

### DOCKER image
Since version `1.6.2` we provide a DOCKER image at [the docker hub: binfalse/bives-webapp](https://hub.docker.com/r/binfalse/bives-webapp/). Using Docker it is very easy and clean to run the web application. do not hesitate to contact us if you need assistance.



## More Information

To learn more about BiVeS


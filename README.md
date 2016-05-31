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
Since version `1.6.2` we provide a DOCKER image at [the docker hub: binfalse/bives-webapp](https://hub.docker.com/r/binfalse/bives-webapp/). This image is automatically generated with every release of the BiVeS web application.

Using Docker it is very easy and clean to run the web application. Do not hesitate to contact us if you need assistance.

## Usage
The BiVeS web application expects an HTTP POST request with a job encoded in JSON. To for example compare the two files at `http://budhat.sems.uni-rostock.de/download?downloadModel=24` and `http://budhat.sems.uni-rostock.de/download?downloadModel=25` asking for the HTML report you could launch the following `curl` call:

    curl -d '{
    	"files":
    	[
    		"http://budhat.sems.uni-rostock.de/download?downloadModel=24",
    		"http://budhat.sems.uni-rostock.de/download?downloadModel=25"
    	],
    	"commands":
    	[
    		"reportHtml"
    	]
    }' http://YOUR.BIVES.WEBAPP

Just replace `http://YOUR.BIVES.WEBAPP` with the URL to your installation of the BiVeS WebApp (or take our installation at http://bives.sems.uni-rostock.de).

You'll get more information about the usage and possible options/arguments by browsing to that installation with using your preferred web browser, [see our installation for example](http://bives.sems.uni-rostock.de).

There is a [Java WebApp client](https://github.com/binfalse/BiVeS-WebApp-Client) for accessing the BiVeS WebApp from java based tools.

## More Information

To learn more about BiVeS have a look at the [BiVeS project](https://github.com/binfalse/BiVeS/) and our [open-access scientific publication](http://bioinformatics.oxfordjournals.org/content/32/4/563.full.pdf+html).


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

### Docker image
Since version `1.6.2` we provide a Docker image at [the docker hub: binfalse/bives-webapp](https://hub.docker.com/r/binfalse/bives-webapp/).
The image is based on an [Apache Tomcat image](https://hub.docker.com/r/library/tomcat/) and already has the BiVeS web application deployed as the default webapp of the Tomcat.

It will be automatically generated with every git-commit to this repository.
In addition, we are generated a new version with every release of the BiVeS web application [through maven](https://binfalse.de/2016/05/31/mvn-automagically-create-a-docker-image/)..

Using Docker it is very easy and clean to run the web application.
Just call

    docker run -it --rm -p 1234:8080 binfalse/bives-webapp

and a Tomcat will start and bind to port `1234` of your machine.
Go ahead and open [http://localhost:1234](http://localhost:1234) to see if it's working! :)

Do not hesitate to contact us if you need assistance.


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


## Monitoring

If you try to access `/` through an HTTP GET request BiVeS will return a `400 Bad Request` status, signalling applications that something's missing and show a *usage* page in your web browser.
That's of course pretty difficult to monitoring for health..
Therefore we implemented `/status` that will return `200 OK` and tell you the BiVeS version that it's running at the moment.
You could even evaluate it's content and check for the phrase *running just fine* :)


## Request for comments

We are always keen on getting feedback from our users. If you have any comments, requests for features, or experience any problems do not hesitate to leave a comment.


## More Information

To learn more about BiVeS have a look at the [BiVeS project](https://github.com/binfalse/BiVeS/) and our [open-access scientific publication](http://bioinformatics.oxfordjournals.org/content/32/4/563.full.pdf+html).


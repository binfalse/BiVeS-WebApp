# BiVeS-WebApp
This is the BiVeS web application, a JAVA based web interface to the ![bives logo](https://sems.uni-rostock.de/wp-content/uploads/2012/12/logo-icon-16.png) [BiVeS tool](https://github.com/binfalse/BiVeS) for difference detection between versions of computational models.

## BiVeS consists of several modules

![bives modules](https://github.com/binfalse/BiVeS/blob/master/art/dependency-graph.png)

BiVeS itself consists of a number of modules:

* [xmlutils](https://github.com/binfalse/xmlutils) is a library for advanced XML handling
* [jCOMODI](https://github.com/binfalse/jCOMODI/) provides programmatic access to the [COMODI ontology](http://purl.uni-rostock.de/comodi/)
* [BiVeS-Core](https://github.com/binfalse/BiVeS-Core) is the core library for comparison of computational models
* [BiVeS-SBML](https://github.com/binfalse/BiVeS-SBML/) is a module providing special strategies for models encoded in SBML
* [BiVeS-CellML](https://github.com/binfalse/BiVeS-CellML) is a module providing special strategies for models encoded in CellML
* [BiVeS](https://github.com/binfalse/BiVeS) ties all modules together and provides command line access
* [BiVeS-WebApp](https://github.com/binfalse/BiVeS-WebApp) is a web interface to access BiVeS through the network
* [BiVeS-WebApp-Client](https://github.com/binfalse/BiVeS-WebApp-Client) provides a Java library for comparing models using the BiVeS-WebApp


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

You should, however, also mount a config file into the container at `/usr/local/tomcat/conf/Catalina/localhost/ROOT.xml`, which tells the visitors who's running the website (including a link to an imprint):

    docker run -it --rm -p 1234:8080 -v /path/to/your.xml:/usr/local/tomcat/conf/Catalina/localhost/ROOT.xml binfalse/bives-webapp

An example config file can be found at [`src/main/docker/BiVeS-WebApp-DockerContext.xml`](src/main/docker/BiVeS-WebApp-DockerContext.xml).

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

The [GitHub pages of the SEMS project](https://semsproject.github.io/BiVeS-WS/) provide mode information on the BiVeS WebApp.


## LICENSE

Artwork and text etc is licensed under a [Creative Commons Attribution-ShareAlike 4.0 International License](http://creativecommons.org/licenses/by-sa/4.0/) ![Creative Commons License](https://i.creativecommons.org/l/by-sa/4.0/80x15.png)

The code is licensed under an [Apache 2.0 license](LICENSE):

    Copyright martin scharm <https://binfalse.de/contact/>

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.



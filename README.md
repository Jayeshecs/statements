# Statements Manager

A Java/J2EE based web application to manage financial transactions e.g. Bank accounts, Credit card, Wallets, etc.

This project is open source under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)

[![Build Status](https://travis-ci.com/Jayeshecs/statements.svg?branch=master)](https://travis-ci.com/Jayeshecs/statements)
[![Documentation Status](https://readthedocs.org/projects/statements/badge/?version=latest)](https://statements.readthedocs.io/en/latest/?badge=latest)

# Building - Statements Manager

### Pre-requisites
- Java 8+
- Maven 3.2.1+

### Building for the first time

- Execute below command from base directory where you have cloned statements repository

```
mvn clean install
```

> This can take sometime to download all dependencies, so let it happen in background and check after 15-20 mins time.

### Building offline

```
mvn clean install -o
```

### Running application

```
mvn -pl webapp jetty:run
```

### Accessing Application

- In Web browser open link [http://localhost:8080/wicket](http://localhost:8080/wicket)
- Use below credentials to login
-- username: `sven`
-- password: `pass`

## More advanced use cases

### To enhance all JDO entities prior to running: +

```
mvn -pl module-simple datanucleus:enhance -o
```

### Run as Standalone JAR

To generate the link: [Jetty console](https://github.com/eirbjo/jetty-console), allowing the application to run as a standalone JAR: 

```
mvn install -Dmavenmixin-jettyconsole
```

This can then be run using:

```
cd webapp/target && java -jar statements-webapp-xxx-jetty-console.war
```

> Add additional arguments ``--headless`` or ``--port 9999``, if required.

It can also be run using:

```
mvn -pl webapp && mvn antrun:run -Dmavenmixin-jettyconsole
```

or to specify arguments:

```
mvn -pl webapp && mvn antrun:run -Dmavenmixin-jettyconsole -Dmaven-antrun-plugin.jettyconsole.args="--headless --port 9090"
```


### Generating Javadoc

- To also generate source and javadoc JARs:

```
mvn clean package -Dmavenmixin-sourceandjavadoc
```

> This is configured only for the domain modules.

- To create the website (for the a domain module e.g. module-static) with source/javadoc and static analysis:

```
mvn -pl module-static site -Dmavenmixin-sourceandjavadoc -Dmavenmixin-staticanalysis
```

> This is intended to run only for the domain modules, and will generate a website under`target/site/index.html`.

### Disable Tests
- To disable the running of unit tests:

```
mvn -DskipUTs 
```

> By default, output of unit tests are in  `target/surefire-unittest-reports` (in the ``module-simple`` modules).

- To disable the running of integration tests:

```
mvn -DskipITs 
```

> By default, output of integration tests are in  `target/surefire-integtest-reports` (in the ``application`` and ``module-simple`` modules).

- To disable the running of BDD specs:

```
mvn -DskipBSs 
```

> By default, output of integration tests are in  `target/surefire-integbddspecs-reports` (in the ``application`` module).

- To disable the running of all tests and BDD specs:

```
mvn -DskipTests 
```

- To disable the running of the `isis:validate` goal:

```
mvn -Dskip.mavenmixin-isisvalidate  
```


- To disable the generation of cucumber reports:

```
mvn -Dskip.mavenmixin-cucumberreporting
```

> By default, cucumber reports are generated at `target/cucumber-html-reports/overview-features.html` (in the ``application`` module).

### Disable Swagger

- To disable the running of the `isis:swagger` goal:

```
mvn -Dskip.mavenmixin-isisswagger
```

> By default, Swagger schema definition files are generated at `target/generated-resources/isis-swagger` (in the ``webapp`` module).

### Docker

- To package up the application as a docker image (specifying the docker image name as a system property):

```
mvn install -Dmavenmixin-docker -Ddocker-plugin.imageName=jayeshecs/statements
```

Alternatively, define the `${docker-plugin.imageName}` in the `webapp` module and use simply:

```
mvn install -Dmavenmixin-docker
```

> The packaged image can be viewed using `docker images`.

- To run a docker image previously packaged:

```
docker container run -d -p 8080:8080 jayeshecs/statements
```

This can then be accessed at link:[localhost:8080](http://localhost:8080).

See link:[mavenmixin-docker](https://github.com/danhaywood/java-mavenmixin-docker#how-to-consume) for further details on how to run docker images.

- To upload the application as a docker image to link: [docker hub](https://hub.docker.com) (or some other docker registry):

```
mvn -pl webapp deploy -Dmavenmixin-docker
```

This assumes that the `${docker-plugin.imageName}` property has been defined, _and_ also that docker registry credentials have been specified in `~/.m2/settings.xml`.

Once more, see link: [mavenmixin-docker](https://github.com/danhaywood/java-mavenmixin-docker#how-to-configure) for further details.


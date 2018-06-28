Simple Spring Boot app that runs on Google AppEngine. No attempt has been made to use the Google APIs - just a minimal Spring app that works.

Google App Engine Java 8 Runtime with Servlet 3.1 does NOT require [spring-boot-legacy](https://github.com/scratches/spring-boot-legacy)!

To Test Locally:
```
$ git clone https://github.com/dyser/spring-boot-legacy
$ cd spring-boot-legacy
$ mvn install
$ cd spring-boot-samples/spring-boot-sample-gae-3.1
$ mvn appengine:devserver
```

To Deploy:
```
$ git clone https://github.com/dyser/spring-boot-legacy
$ cd spring-boot-legacy
$ mvn install
$ cd spring-boot-samples/spring-boot-sample-gae-3.1
$ mvn appengine:update
```

To Deploy to specific AppEngine Project and Version:
```
$ git clone https://github.com/dyser/spring-boot-legacy
$ cd spring-boot-legacy
$ mvn clean install
$ cd spring-boot-samples/spring-boot-sample-gae-3.1
$ mvn appengine:update -Dgae.appId=${APP_ENGINE_PROJECT_ID} -Dgae.version=${APP_ENGINE_PROJECT_VERSION}
$ 
```

Also runs as a deployed WAR in WTP or regular Tomcat container. The `main()` app (normal Spring Boot launcher) should also work.

> NOTE: Google AppEngine does not allow JMX, so you have to switch it off in a Spring Boot app (`spring.jmx.enabled=false`).

> WARNING: Spring Boot manages the appengine API version, which is nice. This project overrides it by virtue of using the `spring-boot-starter-parent` and defining the `appengine.version`. But beware, because `appengine.version` has a specific (other) meaning to the appengine Maven plugin, so we also have to define `gae.version` and use that to set the application version in the plugin configuration.

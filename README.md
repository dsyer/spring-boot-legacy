Spring Boot Support for Servlet 2.5
===================================

Latest release 2.0.0.RELEASE, updated to Spring Boot 2.0.3.RELEASE.

Spring Boot is built on Servlet 3.1. Older servlet versions can be
used with Spring Boot, but some workarounds are needed. This project
is a library that gives you a start with those. There is a sample that
is running on Google Appengine at http://dsyerboot.appspot.com/. Copy
the `web.xml` from the sample to get started with your own project.

To deploy the sample to GAE use `mvn appengine:update` (reference docs
are
[here](https://cloud.google.com/appengine/docs/java/tools/maven#app_engine_maven_plugin_goals)).

Additionally this can be used to load Spring Boot in a Servlet 3.0+ container like Wildfly 8.2.1 when
you exclude the Spring Framework and Spring Boot dependencies from the WAR and place them in the EAR.  i.e. A Skinny Ear.
See [Spring Boot EAR with Skinny WARs](https://github.com/ddcruver/spring-boot-ear-skinny-war).

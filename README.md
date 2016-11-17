Spring Boot Support for Servlet 2.5
===================================

Latest release 1.1.0.RELEASE, updated to Spring Boot 1.4.2.RELEASE.

Spring Boot is built on Servlet 3.1. Older servlet versions can be
used with Spring Boot, but some workarounds are needed. This project
is a library that gives you a start with those. There is a sample that
is running on Google Appengine at http://dsyerboot.appspot.com/. Copy
the `web.xml` from the sample to get started with your own project.

To deploy the sample to GAE use `mvn appengine:update` (reference docs
are
[here](https://cloud.google.com/appengine/docs/java/tools/maven#app_engine_maven_plugin_goals)).

Simple Spring Boot app that runs on Google AppEngine. No attempt has been made to use the Google APIs - just a minimal Spring app that works.

Depends on [spring-boot-legacy](https://github.com/scratches/spring-boot-legacy) (which you will need to build and install locally):

```
$ git clone https://github.com/scratches/spring-boot-legacy
$ (cd spring-boot-legacy; mvn install)
$ cd spring-boot-sample-gae
$ mvn appengine:update
```

It will be deployed to the settings in appengine-web.xml
```  
	<application>my-project</application>
	<module>spring-boot-sample-gae</module>
	<version>1</version>
```


Note that web.xml contains a sub-classed SpringBootContextLoaderListener which you configure
```  
<listener>  
	<listener-class>demo.config.CustomSpringBootContextLoaderListener</listener-class>  
</listener>  
```  

Also runs as a deployed WAR in WTP or regular Tomcat container. The `main()` app (normal Spring Boot launcher) should also work.


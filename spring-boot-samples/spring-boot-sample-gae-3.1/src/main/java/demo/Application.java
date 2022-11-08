/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package demo;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableAutoConfiguration
public class Application extends SpringBootServletInitializer {

	private static final java.util.logging.Logger logger = Logger.getLogger(Application.class.getCanonicalName());

	@Autowired
	private ServletContext context;

	@Value("${info.version}")
	private String version;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);

			StringBuilder beans = new StringBuilder();

			beans.append("Spring Boot Beans: ");

			for (String beanName : beanNames) {
				beans.append(beanName);
				beans.append(System.lineSeparator());
			}

			if(logger.isLoggable(Level.SEVERE)) {
				logger.fine(beans.toString());
			}
		};
	}

	@RequestMapping("/")
	public String home() {
		return "Spring Boot Sample - Google AppEngine - Java 8 Runtime with Servlet 3.1 Web Descriptor";
	}

	@RequestMapping("/version")
	public String getVersion() {
		return version;
	}

	@RequestMapping("/servlet-version")
	public String getServletVersion() {
		return context.getEffectiveMajorVersion() + "." + context.getEffectiveMinorVersion();
	}

}

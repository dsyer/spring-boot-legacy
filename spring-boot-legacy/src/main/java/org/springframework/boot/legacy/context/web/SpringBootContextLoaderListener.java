/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.legacy.context.web;

import javax.servlet.ServletContext;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

/**
 * A {@link ContextLoaderListener} that uses {@link SpringApplication} to initialize an
 * application context. Allows Servlet 2.5 applications (with web.xml) to take advantage
 * of all the initialization extras in Spring Boot even if they don't use an embedded
 * container.
 * 
 * @author Dave Syer
 */
public class SpringBootContextLoaderListener extends ContextLoaderListener {

	private static final String INIT_PARAM_DELIMITERS = ",; \t\n";

	@Override
	public WebApplicationContext initWebApplicationContext(
			final ServletContext servletContext) {
		String configLocationParam = servletContext
				.getInitParameter(CONFIG_LOCATION_PARAM);
		SpringApplicationBuilder builder = new SpringApplicationBuilder(
				(Object[]) StringUtils.tokenizeToStringArray(configLocationParam,
						INIT_PARAM_DELIMITERS));
		@SuppressWarnings("unchecked")
		Class<? extends ConfigurableApplicationContext> contextClass = (Class<? extends ConfigurableApplicationContext>) determineContextClass(servletContext);
		builder.contextClass(contextClass);
		builder.initializers(new ApplicationContextInitializer<GenericWebApplicationContext>() {
			@Override
			public void initialize(GenericWebApplicationContext applicationContext) {
				applicationContext.setServletContext(servletContext);
			}
		});
		WebApplicationContext context = (WebApplicationContext) builder.run();
		servletContext.setAttribute(
				WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
		return context;
	}

	@Override
	protected Class<?> determineContextClass(ServletContext servletContext) {
		String contextClassName = servletContext.getInitParameter(CONTEXT_CLASS_PARAM);
		if (contextClassName != null) {
			try {
				return ClassUtils.forName(contextClassName, null);
			}
			catch (Exception e) {
				throw new ApplicationContextException(
						"Failed to load custom context class [" + contextClassName + "]",
						e);
			}
		}
		return AnnotationConfigNonEmbeddedWebApplicationContext.class;
	}

}

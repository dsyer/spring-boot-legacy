/*
 * Copyright 2012-2018 the original author or authors.
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

import java.util.Arrays;
import java.util.Collections;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.ParentContextApplicationContextInitializer;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.legacy.context.web.servlet.support.ErrorPageFilterConfiguration;
import org.springframework.boot.web.servlet.support.ErrorPageFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.StandardServletEnvironment;

/**
 * A {@link ContextLoaderListener} that uses {@link SpringApplication} to initialize an
 * application context. Allows Servlet 2.5 applications (with web.xml) to take advantage
 * of all the initialization extras in Spring Boot even if they don't use an embedded
 * container.
 *
 * @author Daniel Cruver
 * @author Dave Syer
 */
public class SpringBootContextLoaderListener extends ContextLoaderListener {

	/**
	 * Name of servlet context parameter (i.e., {@value}) that can specify to
	 * disable registration of error page filter.
	 *
	 * @see org.springframework.boot.web.servlet.support.SpringBootServletInitializer#setRegisterErrorPageFilter(boolean)
	 */
	public static final String SPRING_BOOT_LEGACY_REGISTER_ERROR_PAGE_FILTER_PARAM = "springBootLegacyRegisterErrorPageFilter";

	private static final String INIT_PARAM_DELIMITERS = ",; \t\n";

	protected Log logger; // Don't initialize early

	private boolean registerErrorPageFilter = true;

	/**
	 * Set if the {@link ErrorPageFilter} should be registered. Set to {@code false} if
	 * error page mappings should be handled via the server and not Spring Boot.
	 *
	 * This method is a clone from {@link org.springframework.boot.web.servlet.support.SpringBootServletInitializer} but since
	 * we are initializing it differently, we can not call this method on the {@link ContextLoaderListener} a servlet context
	 * param {@link #SPRING_BOOT_LEGACY_REGISTER_ERROR_PAGE_FILTER_PARAM} has been provided for setting this value.
	 *
	 * @param registerErrorPageFilter if the {@link ErrorPageFilter} should be registered.
	 */
	protected final void setRegisterErrorPageFilter(boolean registerErrorPageFilter) {
		this.registerErrorPageFilter = registerErrorPageFilter;
	}

	@Override
	public WebApplicationContext initWebApplicationContext(
			final ServletContext servletContext) {
		this.logger = LogFactory.getLog(getClass());
		this.logger.debug("Initializing WebApplicationContext");
		String configLocationParam = servletContext.getInitParameter(CONFIG_LOCATION_PARAM);
		String[] classNames = StringUtils.tokenizeToStringArray(configLocationParam, INIT_PARAM_DELIMITERS);

		setRegisterErrorPageFilterFromContextParam(servletContext);

		SpringApplicationBuilder builder = createSpringApplicationBuilder(classNames);

		StandardServletEnvironment environment = new StandardServletEnvironment();
		environment.initPropertySources(servletContext, null);
		builder.environment(environment);

		setMainClass(builder, classNames);

		@SuppressWarnings("unchecked")
		Class<? extends ConfigurableApplicationContext> contextClass = (Class<? extends ConfigurableApplicationContext>) determineContextClass(servletContext);
		builder.contextClass(contextClass);

		ApplicationContext parent = getExistingRootWebApplicationContext(servletContext);

		if (parent != null) {
			this.logger.info("Root context already created (using as parent).");
			servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, null);
			builder.initializers(new ParentContextApplicationContextInitializer(parent));
		}

		builder.initializers((ApplicationContextInitializer<ConfigurableWebApplicationContext>) applicationContext -> applicationContext.setServletContext(servletContext));

		// Ensure error pages are registered
		if (this.registerErrorPageFilter) {
			builder.sources(ErrorPageFilterConfiguration.class);
		}

		SpringApplication application = builder.build();

		if (application.getAllSources().isEmpty() && AnnotationUtils.findAnnotation(getClass(), Configuration.class) != null) {
			application.addPrimarySources(Collections.singleton(getClass()));
		}

		Assert.state(!application.getAllSources().isEmpty(),
				"No SpringApplication sources have been defined. Either override the "
						+ "configure method or add an @Configuration annotation");

		WebApplicationContext context = (WebApplicationContext) application.run();
		servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
		return context;
	}

	private void setMainClass(SpringApplicationBuilder builder, String[] classNames) {
		try {
			builder.main(Class.forName(classNames[0]));
		}
		catch (ClassNotFoundException e) {
			this.logger.warn("Could create instance of class " + classNames[0] + " provided by " + CONFIG_LOCATION_PARAM, e);
		}
	}

	private void setRegisterErrorPageFilterFromContextParam(ServletContext servletContext) {
		String contextParamValue = servletContext.getInitParameter(SPRING_BOOT_LEGACY_REGISTER_ERROR_PAGE_FILTER_PARAM);

		if (contextParamValue == null) {
			this.logger.debug("No context init parameter found for " + SPRING_BOOT_LEGACY_REGISTER_ERROR_PAGE_FILTER_PARAM + "; leaving it at default value: " + this.registerErrorPageFilter);
		}
		else if (StringUtils.hasText(contextParamValue)) {
			boolean booleanValue = Boolean.parseBoolean(contextParamValue);
			setRegisterErrorPageFilter(booleanValue);
			this.logger.debug("Found context init parameter found for " + SPRING_BOOT_LEGACY_REGISTER_ERROR_PAGE_FILTER_PARAM + "; updating registerErrorPageFilter to " + this.registerErrorPageFilter);
		}
		else {
			this.logger.warn("Context init parameter found for " + SPRING_BOOT_LEGACY_REGISTER_ERROR_PAGE_FILTER_PARAM + " but it is empty; leaving it at default value: " + this.registerErrorPageFilter);
		}
	}

	protected SpringApplicationBuilder createSpringApplicationBuilder(String[] classNames) {

		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Creating SpringApplicationBuilder ( with classes: " + Arrays.toString(classNames) + ")");
		}

		Class[] classes = new Class[classNames.length];
		for (int i = 0; i < classes.length; i++) {
			try {
				classes[i] = ClassUtils.forName(classNames[i], null);
			}
			catch (ClassNotFoundException e) {
				throw new ApplicationContextException(
						"Failed to load custom context class [" + classNames[i] + "]", e);
			}
		}

		return new SpringApplicationBuilder(classes);
	}

	private ApplicationContext getExistingRootWebApplicationContext(ServletContext servletContext) {
		Object context = servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		if (context instanceof ApplicationContext) {
			return (ApplicationContext) context;
		}
		return null;
	}

	@Override
	protected Class<?> determineContextClass(ServletContext servletContext) {
		this.logger = LogFactory.getLog(getClass());
		String contextClassName = servletContext.getInitParameter(CONTEXT_CLASS_PARAM);

		if (contextClassName != null) {
			this.logger.info("Using context class: " + contextClassName);
			try {
				return ClassUtils.forName(contextClassName, null);
			}
			catch (Exception e) {
				throw new ApplicationContextException(
						"Failed to load custom context class [" + contextClassName + "]",
						e);
			}
		}

		logger.debug("Using default context class: " + AnnotationConfigNonEmbeddedWebApplicationContext.class.getCanonicalName() + "");
		return AnnotationConfigNonEmbeddedWebApplicationContext.class;
	}

}

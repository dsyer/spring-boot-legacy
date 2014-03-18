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

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

/**
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
		builder.initializers(new ApplicationContextInitializer<NonEmbeddedWebApplicationContext>() {
			@Override
			public void initialize(NonEmbeddedWebApplicationContext applicationContext) {
				applicationContext.setServletContext(servletContext);
			}
		});
		WebApplicationContext context = (WebApplicationContext) builder.run();
		servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
		return context;
	}

}

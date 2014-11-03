/*
 * Copyright 2013-2014 the original author or authors.
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

import static org.junit.Assert.assertNotNull;

import java.util.Collections;

import javax.servlet.ServletContext;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ContextLoader;

/**
 * @author Dave Syer
 *
 */
public class SpringBootContextLoaderListenerTests {

	private ServletContext servletContext = Mockito.mock(ServletContext.class);

	@Test
	public void test() {
		Mockito.when(servletContext.getInitParameterNames()).thenReturn(
				Collections.emptyEnumeration());
		Mockito.when(servletContext.getAttributeNames()).thenReturn(
				Collections.emptyEnumeration());
		Mockito.when(servletContext.getInitParameter(ContextLoader.CONFIG_LOCATION_PARAM))
				.thenReturn(TestConfiguration.class.getName());
		SpringBootContextLoaderListener listener = new SpringBootContextLoaderListener();
		assertNotNull(listener.initWebApplicationContext(servletContext));
	}

	@Configuration
	protected static class TestConfiguration {

	}

}

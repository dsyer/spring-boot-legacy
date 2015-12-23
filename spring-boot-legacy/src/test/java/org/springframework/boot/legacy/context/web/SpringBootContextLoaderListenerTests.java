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

import java.util.Collections;

import javax.servlet.ServletContext;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.*;

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

    @Test
    public void testNoProfile() {
        Mockito.when(servletContext.getInitParameterNames()).thenReturn(
                Collections.emptyEnumeration());
        Mockito.when(servletContext.getAttributeNames()).thenReturn(
                Collections.emptyEnumeration());
        Mockito.when(servletContext.getInitParameter(ContextLoader.CONFIG_LOCATION_PARAM))
                .thenReturn(TestConfiguration.class.getName());
        SpringBootContextLoaderListener listener = new SpringBootContextLoaderListener();
        WebApplicationContext webApplicationContext = listener.initWebApplicationContext(servletContext);
        assertEquals(webApplicationContext.getEnvironment().getActiveProfiles().length, 0);
    }

    public static class CustomSpringBootContextLoaderListener extends SpringBootContextLoaderListener {
        @Override
        protected void configureSpringApplicationBuilder(SpringApplicationBuilder builder) {
            builder.profiles("uat");
        }
    }

    @Test
    public void testAddsProfile() {
        Mockito.when(servletContext.getInitParameterNames()).thenReturn(
                Collections.emptyEnumeration());
        Mockito.when(servletContext.getAttributeNames()).thenReturn(
                Collections.emptyEnumeration());
        Mockito.when(servletContext.getInitParameter(ContextLoader.CONFIG_LOCATION_PARAM))
                .thenReturn(TestConfiguration.class.getName());
        SpringBootContextLoaderListener listener = new CustomSpringBootContextLoaderListener();
        WebApplicationContext webApplicationContext = listener.initWebApplicationContext(servletContext);
        assertEquals(webApplicationContext.getEnvironment().getActiveProfiles().length, 1);
        assertEquals(webApplicationContext.getEnvironment().getActiveProfiles()[0], "uat");
    }



}

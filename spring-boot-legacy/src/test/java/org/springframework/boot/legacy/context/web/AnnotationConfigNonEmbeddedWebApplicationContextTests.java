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

import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * @author Dave Syer
 *
 */
public class AnnotationConfigNonEmbeddedWebApplicationContextTests {

	@Test
	public void test() {
		ConfigurableApplicationContext context = new SpringApplicationBuilder(
				TestConfiguration.class).contextFactory(type ->
				new AnnotationConfigNonEmbeddedWebApplicationContext()).run();
		context.close();
	}

	@Configuration
	protected static class TestConfiguration {

	}

}

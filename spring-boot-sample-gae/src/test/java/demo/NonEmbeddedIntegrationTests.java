/*
 * Copyright 2012-2013 the original author or authors.
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

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.test.TestRestTemplate;

public class NonEmbeddedIntegrationTests {

	private static final Logger log = Logger.getLogger(NonEmbeddedIntegrationTests.class);

	@Rule
	public ServerRunning serverRunning = ServerRunning.isRunning();

	private int port = 8080;

	@Test
	public void testVersion() throws IOException {
		String body = new TestRestTemplate().getForObject("http://localhost:" + port
				+ "/info", String.class);
		log.info("found info = " + body);
		assertTrue("Wrong body: " + body, body.contains("{\"version"));
	}

	@Test
	public void testMetrics() throws IOException {
		String body = new TestRestTemplate().getForObject("http://localhost:" + port
				+ "/metrics", String.class);
		log.info("found metrics = " + body);
		assertTrue("Wrong body: " + body, body.contains("\"classes.loaded"));
	}

}

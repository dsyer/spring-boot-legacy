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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertTrue;

import demo.hello.Greeting;
import demo.hello.HelloWorldController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = Application.class)
public class EmbeddedIntegrationTests {

	private static final Log logger = LogFactory.getLog(EmbeddedIntegrationTests.class);

	private static final boolean TEST_WITH_SECURITY = true;

	@Value("${info.version}")
	private String expectedVersion;

	private String password;

	@Value("${local.server.port}")
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired(required = false)
	private InMemoryUserDetailsManager userDetailsManager;

	private String username = "user";

	@Before
	public void setup() {

		if(TEST_WITH_SECURITY) {
			// Get password from userDetailsManager, currently this is hardcoded in application.properties but
			// this is especially important when it is set to be automatically generated.
			UserDetails userDetails = userDetailsManager.loadUserByUsername(username);
			password = userDetails.getPassword().replace("{noop}", "");
			BasicAuthorizationInterceptor bai = new BasicAuthorizationInterceptor(username, password);
			restTemplate.getRestTemplate().getInterceptors().add(bai);
		}
	}

	@Test
	public void testActuatorConditionsEndpoint() {
		String body = restTemplate.getForObject("http://127.0.0.1:" + port + "/version", String.class);
		logger.debug("found version = " + body);
		String response = restTemplate.getForObject("http://127.0.0.1:" + port + "/actuator/conditions", String.class);
		logger.debug(response);
	}

	@Test
	public void testHelloWorldCounter() {
		long expectedValue = 0;

		for (int i = 0; i < 10; i++) {
			expectedValue++;

			Greeting greeting = restTemplate.getForObject("http://127.0.0.1:" + port + "/hello-world", Greeting.class);
			logger.debug("greeting = " + greeting);

			// Hit endpoint and check counter.
			assertThat(greeting, is(not(nullValue())));
			assertThat(greeting.getContent(), is("Hello, Stranger!"));
			// Check our own internal counter, this is just a sanity check
			assertThat(greeting.getId(), is(expectedValue));

			// Verify metrics are being updated.
			MetricsEndpoint.MetricResponse response = restTemplate.getForObject("http://127.0.0.1:" + port + "/actuator/metrics/" + HelloWorldController.HELLO_WORLD_COUNTER, MetricsEndpoint.MetricResponse.class);
			MetricsEndpoint.Sample sample = response.getMeasurements().get(0);
			assertThat((sample.getValue()), is((double) expectedValue));
		}

		String response = restTemplate.getForObject("http://127.0.0.1:" + port + "/actuator/metrics/http.server.requests", String.class);

		logger.info(response);
	}

	@Test
	public void testVersion() {
		//mvc.perform(get("/"))
		String body = restTemplate.getForObject("http://127.0.0.1:" + port + "/version", String.class);
		logger.debug("found version = " + body);
		assertTrue("Wrong body: " + body, body.contains(expectedVersion));
	}
}

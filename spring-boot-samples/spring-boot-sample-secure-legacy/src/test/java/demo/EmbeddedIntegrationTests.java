/*
 * Copyright 2012-2018 the original author or authors.
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

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = Application.class)
public class EmbeddedIntegrationTests {

	private static final Logger log = LoggerFactory.getLogger(EmbeddedIntegrationTests.class);

	@Autowired
	private TestRestTemplate restTemplate;

	@Value("${local.server.port}")
	private int port;

	@Value("${message}")
	private String expectedMessage;

	@Value("${info.version}")
	private String expectedVersion;

	@Value("${spring.security.user.name}")
	private String username;

	@Value("${spring.security.user.password}")
	private String password;

	@Test
	public void testSecureRedirectToLoginPage() {
		ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://127.0.0.1:" + port + "/", String.class);
		String body = responseEntity.getBody();
		assertEquals("Wrong body: " + body, HttpStatus.FOUND, responseEntity.getStatusCode());
		assertEquals("Not Login Page", "http://127.0.0.1:" + port + "/login", responseEntity.getHeaders().get("Location").get(0));
	}

	@Test
	public void testSecureAuthenticated() {
		String plainCreds = username + ":" + password;
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);

		HttpEntity<String> request = new HttpEntity<>(headers);

		ResponseEntity<String> responseEntity = restTemplate.exchange("http://127.0.0.1:" + port  + "/", HttpMethod.GET, request, String.class, new Object[]{} );

		String body = responseEntity.getBody();
		log.info("found / = " + body);
		assertTrue("Wrong body: " + body, body.contains(expectedMessage));
	}

	@Test
	public void testVersion() {
		String plainCreds = username + ":" + password;
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + base64Creds);

		HttpEntity<String> request = new HttpEntity<>(headers);

		ResponseEntity<String> responseEntity = restTemplate.exchange("http://127.0.0.1:" + port  + "/version", HttpMethod.GET, request, String.class, new Object[]{} );
		String body = responseEntity.getBody();

		log.info("found version = " + body);
		assertTrue("Wrong body: " + body, body.contains(expectedVersion));
	}
}

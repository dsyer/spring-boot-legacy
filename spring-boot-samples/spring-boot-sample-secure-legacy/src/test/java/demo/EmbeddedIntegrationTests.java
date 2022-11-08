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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = Application.class)
public class EmbeddedIntegrationTests {

	private static final Log logger = LogFactory.getLog(EmbeddedIntegrationTests.class);

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
		assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode(), "Wrong body: " + body);
		assertEquals("http://127.0.0.1:" + port + "/login", responseEntity.getHeaders().get("Location").get(0), "Not Login Page");
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

		ResponseEntity<String> responseEntity = restTemplate.exchange("http://127.0.0.1:" + port + "/", HttpMethod.GET, request, String.class, new Object[] {});

		String body = responseEntity.getBody();
		logger.info("found / = " + body);
		assertTrue(body.contains(expectedMessage), "Wrong body: " + body);
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

		ResponseEntity<String> responseEntity = restTemplate.exchange("http://127.0.0.1:" + port + "/version", HttpMethod.GET, request, String.class, new Object[] {});
		String body = responseEntity.getBody();

		logger.info("found version = " + body);
		assertTrue(body.contains(expectedVersion), "Wrong body: " + body);
	}
}

/*
 * Copyright 2013-2104 the original author or authors.
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

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assume;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

/**
 * @author Dave Syer
 *
 */
public class ServerRunning implements MethodRule {

	private static Log logger = LogFactory.getLog(ServerRunning.class);

	// Static so that we only test once on failure: speeds up test suite
	private static Map<Integer, Boolean> serverOnline = new HashMap<Integer, Boolean>();

	private static int DEFAULT_PORT = 8080;

	private static String DEFAULT_HOST = "localhost";

	private int port;

	private String hostName = DEFAULT_HOST;

	private String path;

	/**
	 * @return a new rule that assumes an existing running broker
	 */
	public static ServerRunning isRunning(String path) {
		return new ServerRunning(path);
	}

	/**
	 * @return a new rule that assumes an existing running broker
	 */
	public static ServerRunning isRunning() {
		return new ServerRunning("");
	}

	private ServerRunning(String path) {
		setPort(DEFAULT_PORT);
		setPath(path);
	}

	/**
	 * @param path
	 */
	private void setPath(String path) {
		this.path = path;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
		if (!serverOnline.containsKey(port)) {
			serverOnline.put(port, true);
		}
	}

	/**
	 * @param hostName the hostName to set
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public Statement apply(final Statement base, FrameworkMethod method, Object target) {

		// Check at the beginning, so this can be used as a static field
		Assume.assumeTrue(serverOnline.get(port));

		RestTemplate client = new RestTemplate();
		boolean followRedirects = HttpURLConnection.getFollowRedirects();
		HttpURLConnection.setFollowRedirects(false);
		boolean online = false;
		try {
			client.getForEntity(
					new UriTemplate(getUrl("/")).toString(),
					String.class);
			online = true;
			logger.info("Basic connectivity test passed");
		}
		catch (RestClientException e) {
			logger.warn(
					String.format(
							"Not executing tests because basic connectivity test failed for hostName=%s, port=%d",
							hostName, port), e);
			Assume.assumeNoException(e);
		}
		finally {
			HttpURLConnection.setFollowRedirects(followRedirects);
			if (!online) {
				serverOnline.put(port, false);
			}
		}

		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				base.evaluate();
			}
		};

	}

	public String getUrl(String path) {
		if (path.startsWith("http")) {
			return path;
		}
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		return "http://" + hostName + ":" + port + this.path + path;
	}

}

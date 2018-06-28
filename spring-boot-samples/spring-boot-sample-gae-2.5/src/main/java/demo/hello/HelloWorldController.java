/*
 * Copyright 2018 the original author or authors.
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

package demo.hello;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

	public static final String HELLO_WORLD_COUNTER = "hello-world.requested";

	private static final String HELLO_TEMPLATE = "Hello, %s!";

	private static final java.util.logging.Logger logger = Logger.getLogger(HelloWorldController.class.getCanonicalName());

	private final Counter counter;

	private final AtomicInteger internalCounter;

	public HelloWorldController(MeterRegistry registry) {

		this.counter = registry.counter(HELLO_WORLD_COUNTER);
		this.internalCounter = new AtomicInteger();
	}

	@Timed
	@GetMapping(value = "/hello-world", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public Greeting sayHello(
			@RequestParam(name = "name", required = false, defaultValue = "Stranger") String name) {

		logger.info("Called Hello Endpoint");
		counter.increment();
		return new Greeting(internalCounter.incrementAndGet(), String.format(HELLO_TEMPLATE, name));
	}
}

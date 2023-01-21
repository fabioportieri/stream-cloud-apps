/*
 * Copyright 2020-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.app.processor.twitter.trend.location;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.verify.VerificationTimes.once;
import static org.springframework.cloud.stream.app.processor.twitter.trend.location.TestTwitterTrendLocationProcessorApplication.setExpectation;

/**
 * @author Christian Tzolov
 */
public class TwitterTrendLocationProcessorAvailableTrendsTests {

	@Test
	public void testTwitterAvailableTrends() {
		ClientAndServer mockServer = ClientAndServer.startClientAndServer(TestTwitterTrendLocationProcessorApplication.MOCK_SERVER_PORT);
		MockServerClient mockClient = new MockServerClient(TestTwitterTrendLocationProcessorApplication.MOCK_SERVER_IP, TestTwitterTrendLocationProcessorApplication.MOCK_SERVER_PORT);

		HttpRequest availableTrendsRequest = setExpectation(request()
				.withMethod("GET")
				.withPath("/trends/available.json"), mockClient);

		try (ConfigurableApplicationContext context = new SpringApplicationBuilder(
				TestChannelBinderConfiguration.getCompleteConfiguration(TestTwitterTrendLocationProcessorApplication.class))
				.web(WebApplicationType.NONE)
				.run("--spring.cloud.function.definition=twitterTrendFunction",

						"--twitter.trend.trendQueryType=trendLocation",
						"--twitter.connection.rawJson=false",

						"--twitter.connection.consumerKey=myConsumerKey",
						"--twitter.connection.consumerSecret=myConsumerSecret",
						"--twitter.connection.accessToken=myAccessToken",
						"--twitter.connection.accessTokenSecret=myAccessTokenSecret")) {

			InputDestination input = context.getBean(InputDestination.class);
			OutputDestination output = context.getBean(OutputDestination.class);
			assertThat(input).isNotNull();
			assertThat(output).isNotNull();

			input.send(new GenericMessage<>("hello".getBytes(StandardCharsets.UTF_8)));

			Message<byte[]> outputMessage = output.receive(Duration.ofSeconds(300).toMillis(), "twitterTrendFunction-out-0");
			assertThat(outputMessage).isNotNull();

			mockClient.verify(availableTrendsRequest, once());
			assertThat(outputMessage);

			String payload = new String(outputMessage.getPayload());
			assertThat(payload).containsSequence("countryName");
			assertThat(payload).contains("placeCode");
			assertThat(payload).doesNotContain("placeType");
		}
		finally {
			mockServer.stop();
		}
	}
}

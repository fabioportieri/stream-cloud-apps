/*
 * Copyright 2020-2020 the original author or authors.
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

package org.springframework.cloud.stream.app.test.integration.junit.jupiter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Supplier;

import org.springframework.core.annotation.AliasFor;

import static org.springframework.cloud.stream.app.test.integration.StreamAppContainerTestUtils.SPRINGCLOUDSTREAM_REPOSITOTRY;

/**
 * Annotation For configuring a {@link org.springframework.cloud.stream.app.test.integration.kafka.KafkaStreamAppContainer}.
 * @author David Turanski
 * @see org.springframework.cloud.stream.app.test.integration.junit.jupiter.BaseContainer
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@BaseContainer(binder = Binder.Kafka)
public @interface KafkaBaseContainer {

	@AliasFor(annotation = BaseContainer.class)
	String version() default "";

	@AliasFor(annotation = BaseContainer.class)
	Class<? extends Supplier<String>> versionSupplier() default NullVersionSupplier.class;

	@AliasFor(annotation = BaseContainer.class)
	String name() default "";

	@AliasFor(annotation = BaseContainer.class)
	String repository() default SPRINGCLOUDSTREAM_REPOSITOTRY;
}

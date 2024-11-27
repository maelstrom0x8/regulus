/*
 * Copyright (C) 2024 Emmanuel Godwin
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
package io.ceze.config;

import io.ceze.gis.GeoApiContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RegulusConfiguration
{

	@Value("${regulus.providers.gis.api-key}")
	private String geoCodeApiKey;

	@Value("${spring.mail.host}")
	private String mailHost;

	@Value("${spring.mail.port}")
	private String mailPort;

	@Bean
	public GeoApiContext geoApiContext()
	{
		return new GeoApiContext.Builder().apiKey(geoCodeApiKey).build();
	}

	@Bean
	JavaMailSender mailSender()
	{
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(mailHost);
		mailSender.setPort(Integer.parseInt(mailPort));
		return mailSender;
	}

	@Bean
	RestTemplate restTemplate(RestTemplateBuilder builder)
	{
		return builder.build();
	}
}

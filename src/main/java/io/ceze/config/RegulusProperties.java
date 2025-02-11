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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "regulus")
public class RegulusProperties
{

	private Map<String, Provider> providers = new HashMap<>();

	public Map<String, Provider> getProviders()
	{
		return providers;
	}

	public void setProviders(Map<String, Provider> providers)
	{
		this.providers = providers;
	}

	public static class Provider
	{
		private String name;
		private String apiKey;
		private String baseUrl;

		public String getName()
		{
			return name;
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public String getApiKey()
		{
			return apiKey;
		}

		public void setApiKey(String apiKey)
		{
			this.apiKey = apiKey;
		}

		public String getBaseUrl()
		{
			return baseUrl;
		}

		public void setBaseUrl(String baseUrl)
		{
			this.baseUrl = baseUrl;
		}
	}
}

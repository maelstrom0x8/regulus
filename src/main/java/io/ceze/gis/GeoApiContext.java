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
package io.ceze.gis;

/**
 * GeoApiContext is a configuration class for setting up API keys
 * and other parameters required to make requests to geographic APIs.
 */
public class GeoApiContext {
    private String apiKey;

    private GeoApiContext() {}

    public static class Builder {
        private final GeoApiContext context = new GeoApiContext();

        public Builder apiKey(String apiKey) {
            context.apiKey = apiKey;
            return this;
        }

        public GeoApiContext build() {
            if (context.apiKey.isBlank()) {
                throw new RuntimeException("Cannot use blank key in the context");
            }
            return this.context;
        }
    }

    public String getApiKey() {
        return apiKey;
    }
}
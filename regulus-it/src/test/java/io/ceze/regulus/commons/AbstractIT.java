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
package io.ceze.regulus.commons;

import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;


public class AbstractIT extends ApplicationContainerConfig {

    protected static HttpClient client;
    protected static String baseUri;
    static final String serviceName = "aeros-service";
    static final int targetPort = 8080;

//    static RestAssured restAssured;

    static final Logger LOGGER = LoggerFactory.getLogger(AbstractIT.class);


    @BeforeAll
    static void initAll() {
//        String host = payaraServerContainer.getHost();
//        Integer firstMappedPort = payaraServerContainer.getFirstMappedPort();
//
//        String template = "http://%s:%d";
//        baseUri = String.format(template, host, firstMappedPort);
//        client = HttpClient.newBuilder().build();
//        RestAssured.baseURI = baseUri;
    }
}

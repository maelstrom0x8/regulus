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
package io.ceze.regulus;

import io.restassured.RestAssured;
import jakarta.json.bind.Jsonb;
import java.net.http.HttpClient;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

@Testcontainers
public class AbstractIT {

  protected static HttpClient client;
  protected static String baseUri;
  static final String serviceName = "aeros-service";
  static final int targetPort = 8080;
  static Jsonb jsonb;

  static RestAssured restAssured;

  static final Logger LOGGER = LoggerFactory.getLogger(AbstractIT.class);
  static Slf4jLogConsumer consumer = new Slf4jLogConsumer(LOGGER);

  //    public static Network network = Network.newNetwork();

  //    @Container
  //    protected static PostgreSQLContainer<?> postgreSQLContainer = new
  // PostgreSQLContainer<>("postgres:15")
  //        .withExposedPorts(5432)
  //        .withNetwork(network).withNetworkAliases("pg_network");

  @Container
  public static GenericContainer<?> payaraServerContainer =
      new GenericContainer<>("payara/micro:6.2024.2-jdk21")
          .withCopyFileToContainer(
              MountableFile.forHostPath("build/libs/regulus.war"), "/opt/payara/deployments/")
          .withCommand(
              "--noCluster",
              "--autoBindHttp",
              "--deploymentDir",
              "/opt/payara/deployments",
              "--contextRoot",
              "/",
              "--noHazelCast",
              "--deploy",
              "/opt/payara/deployments/regulus.war")
          .withReuse(true)
          .waitingFor(Wait.forLogMessage("^.*Payara Micro.*ready.+", 1))
          .withLogConsumer(consumer.withSeparateOutputStreams())
          .withExposedPorts(8080);

  @BeforeAll
  static void initAll() {
    String host = payaraServerContainer.getHost();
    Integer firstMappedPort = payaraServerContainer.getFirstMappedPort();

    String template = "http://%s:%d";
    baseUri = String.format(template, host, firstMappedPort);
    client = HttpClient.newBuilder().build();
    RestAssured.baseURI = baseUri;
  }
}

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
package io.ceze.regulus.generator.web.resource;

import io.ceze.regulus.generator.service.WasteService;
import io.ceze.regulus.generator.web.DisposalRequest;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("v1/disposals")
public class WasteDisposalResource {

  private static final Logger LOG = LoggerFactory.getLogger(WasteDisposalResource.class);

  @Inject private WasteService wasteService;

  @Context UriInfo uriInfo;

  @POST
  public Response requestDisposal(@NotNull @RequestBody DisposalRequest request) {
    LOG.info("{}", uriInfo.getRequestUri());
    return Response.ok().build();
  }
}

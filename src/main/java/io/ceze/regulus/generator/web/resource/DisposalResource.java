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

import io.ceze.regulus.generator.service.DisposalResponse;
import io.ceze.regulus.generator.service.DisposalService;
import io.ceze.regulus.generator.service.DisposalStatus;
import io.ceze.regulus.generator.web.DisposalRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.logging.Logger;

@Path("v1/disposals")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class DisposalResource {

    private static final Logger LOG = Logger.getLogger(DisposalResource.class);

    @Inject private DisposalService disposalService;

    @POST
    public Response requestDisposal(@NotNull @Valid @RequestBody DisposalRequest request) {
        //    try {
        LOG.infof("Initiating new disposal request...");
        LOG.infof("request: %s", request);
        DisposalResponse response = disposalService.newDisposalRequest(request);
        LOG.infof("Disposal request successfully initiated");
        return Response.status(Response.Status.CREATED).entity(response).build();
        //    } catch (RuntimeException e) {
        //      LOG.errorf("Error occurred while processing disposal request: %s", e.getMessage());
        //      return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        //    }
    }

    /**
     * Fetches the status of a disposal request with the specified ID.
     *
     * @param id the ID of the disposal request
     * @return a {@code Response} with the status of the disposal request
     */
    @GET
    @Path("/status/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response fetchDisposalStatus(@PathParam("id") Long id) {
        LOG.infof("Fetching status for disposal request with ID: {}", id);
        DisposalStatus status = disposalService.getDisposalStatus(id);
        if (status != null) {
            LOG.infof("Disposal request found with ID: {}. Status: {}", id, status);
            return Response.ok(status).build();
        } else {
            LOG.warnf("No disposal request found with ID: {}", id);
            return Response.status(Response.Status.NOT_FOUND).entity("No such disposal").build();
        }
    }

    @GET
    @Path("/info")
    public String info() {
        LOG.info("Fetching application info");
        return "Regulus Inc 2024";
    }
}

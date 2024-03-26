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
package io.ceze.regulus.account.web.resource;

import io.ceze.regulus.account.service.ProfileService;
import io.ceze.regulus.account.web.ProfileDataRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("v1/users/{user}")
public class ProfileResource {

  @Inject private ProfileService profileService;

  @GET
  public Response fetchProfileData() {
    return Response.ok(profileService.getProfile()).build();
  }

  @POST
  @Path("/update-profile")
  public Response updateProfile(@PathParam("user") String user, ProfileDataRequest request) {
    return Response.status(Response.Status.ACCEPTED).build();
  }
}

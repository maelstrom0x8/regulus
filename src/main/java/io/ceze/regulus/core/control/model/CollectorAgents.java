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
package io.ceze.regulus.core.control.model;

import io.ceze.regulus.integration.geo.Route;
import java.util.Collection;
import java.util.Set;

public class CollectorAgents {

    private final Set<CollectorAgent> agents;

    private CollectorAgents(Set<CollectorAgent> agents) {
        this.agents = agents;
    }

    public static CollectorAgents withAgents(Collection<? extends CollectorAgent> agents) {
        return new CollectorAgents(Set.copyOf(agents));
    }

    public static CollectorAgents withAgent(CollectorAgent agent) {
        return new CollectorAgents(Set.of(agent));
    }

    public void dispatch(Route location) {}
}

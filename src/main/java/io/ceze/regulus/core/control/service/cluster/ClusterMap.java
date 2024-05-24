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
package io.ceze.regulus.core.control.service.cluster;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

class ClusterMap {

    private Map<String, Cluster> clusterMap = new HashMap<>();

    public Cluster check(Predicate<Cluster> condition) {
        return null;
    }
}

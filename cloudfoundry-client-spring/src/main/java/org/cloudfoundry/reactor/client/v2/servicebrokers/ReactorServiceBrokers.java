/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.cloudfoundry.reactor.client.v2.servicebrokers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloudfoundry.client.v2.servicebrokers.CreateServiceBrokerRequest;
import org.cloudfoundry.client.v2.servicebrokers.CreateServiceBrokerResponse;
import org.cloudfoundry.client.v2.servicebrokers.DeleteServiceBrokerRequest;
import org.cloudfoundry.client.v2.servicebrokers.GetServiceBrokerRequest;
import org.cloudfoundry.client.v2.servicebrokers.GetServiceBrokerResponse;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersRequest;
import org.cloudfoundry.client.v2.servicebrokers.ListServiceBrokersResponse;
import org.cloudfoundry.client.v2.servicebrokers.ServiceBrokers;
import org.cloudfoundry.client.v2.servicebrokers.UpdateServiceBrokerRequest;
import org.cloudfoundry.client.v2.servicebrokers.UpdateServiceBrokerResponse;
import org.cloudfoundry.reactor.client.v2.AbstractClientV2Operations;
import org.cloudfoundry.reactor.util.AuthorizationProvider;
import reactor.core.publisher.Mono;
import reactor.io.netty.http.HttpClient;

/**
 * The Reactor-based implementation of {@link ServiceBrokers}
 */
public final class ReactorServiceBrokers extends AbstractClientV2Operations implements ServiceBrokers {

    /**
     * Creates an instance
     *
     * @param authorizationProvider the {@link AuthorizationProvider} to use when communicating with the server
     * @param httpClient            the {@link HttpClient} to use when communicating with the server
     * @param objectMapper          the {@link ObjectMapper} to use when communicating with the server
     * @param root                  the root URI of the server.  Typically something like {@code https://uaa.run.pivotal.io}.
     */
    public ReactorServiceBrokers(AuthorizationProvider authorizationProvider, HttpClient httpClient, ObjectMapper objectMapper, Mono<String> root) {
        super(authorizationProvider, httpClient, objectMapper, root);
    }

    @Override
    public Mono<CreateServiceBrokerResponse> create(CreateServiceBrokerRequest request) {
        return post(request, CreateServiceBrokerResponse.class, builder -> builder.pathSegment("v2", "service_brokers"));
    }

    @Override
    public Mono<Void> delete(DeleteServiceBrokerRequest request) {
        return delete(request, Void.class, builder -> builder.pathSegment("v2", "service_brokers", request.getServiceBrokerId()));
    }

    @Override
    public Mono<GetServiceBrokerResponse> get(GetServiceBrokerRequest request) {
        return get(request, GetServiceBrokerResponse.class, builder -> builder.pathSegment("v2", "service_brokers", request.getServiceBrokerId()));
    }

    @Override
    public Mono<ListServiceBrokersResponse> list(ListServiceBrokersRequest request) {
        return get(request, ListServiceBrokersResponse.class, builder -> builder.pathSegment("v2", "service_brokers"));
    }

    @Override
    public Mono<UpdateServiceBrokerResponse> update(UpdateServiceBrokerRequest request) {
        return put(request, UpdateServiceBrokerResponse.class, builder -> builder.pathSegment("v2", "service_brokers", request.getServiceBrokerId()));
    }

}

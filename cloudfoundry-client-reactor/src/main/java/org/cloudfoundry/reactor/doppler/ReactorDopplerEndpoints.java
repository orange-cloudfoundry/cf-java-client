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

package org.cloudfoundry.reactor.doppler;

import org.cloudfoundry.doppler.ContainerMetricsRequest;
import org.cloudfoundry.doppler.Envelope;
import org.cloudfoundry.doppler.FirehoseRequest;
import org.cloudfoundry.doppler.RecentLogsRequest;
import org.cloudfoundry.doppler.StreamRequest;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.util.Exceptions;
import reactor.io.netty.http.HttpClientResponse;

import java.io.IOException;
import java.io.InputStream;

final class ReactorDopplerEndpoints extends AbstractDopplerOperations {

    ReactorDopplerEndpoints(ConnectionContext connectionContext, Mono<String> root, TokenProvider tokenProvider) {
        super(connectionContext, root, tokenProvider);
    }

    Flux<Envelope> containerMetrics(ContainerMetricsRequest request) {
        return get(builder -> builder.pathSegment("apps", request.getApplicationId(), "containermetrics"))
            .flatMap(inbound -> inbound.receiveMultipart().receiveInputStream())
            .map(ReactorDopplerEndpoints::toEnvelope);
    }

    Flux<Envelope> firehose(FirehoseRequest request) {
        return ws(builder -> builder.pathSegment("firehose", request.getSubscriptionId()))
            .flatMap(HttpClientResponse::receiveInputStream)
            .map(ReactorDopplerEndpoints::toEnvelope);
    }

    Flux<Envelope> recentLogs(RecentLogsRequest request) {
        return get(builder -> builder.pathSegment("apps", request.getApplicationId(), "recentlogs"))
            .flatMap(inbound -> inbound.receiveMultipart().receiveInputStream())
            .map(ReactorDopplerEndpoints::toEnvelope);
    }

    Flux<Envelope> stream(StreamRequest request) {
        return ws(builder -> builder.pathSegment("apps", request.getApplicationId(), "stream"))
            .flatMap(HttpClientResponse::receiveInputStream)
            .map(ReactorDopplerEndpoints::toEnvelope);
    }

    private static Envelope toEnvelope(InputStream inputStream) {
        try (InputStream in = inputStream) {
            return Envelope.from(org.cloudfoundry.dropsonde.events.Envelope.ADAPTER.decode(in));
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }

}
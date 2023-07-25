fdsfs
 * Copyright (c) 2023. Baidu, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.baidu.bifromq.basecluster;

import com.baidu.bifromq.basecluster.memberlist.agent.IAgent;
import com.baidu.bifromq.basecluster.memberlist.agent.IAgentMember;
import com.google.common.collect.Sets;
import com.google.protobuf.ByteString;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.logging.LoggingMeterRegistry;
import io.reactivex.rxjava3.core.Observable;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AgentHostNode2 {
    public static void main(String[] args) {
        Metrics.addRegistry(new LoggingMeterRegistry());

        AgentHostOptions opt = new AgentHostOptions()
            .autoHealingTimeout(Duration.ofSeconds(300))
            .addr("127.0.0.1")
            .port(4445);
        IAgentHost host = IAgentHost.newInstance(opt);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            host.shutdown();
        }));
        host.start();

        host.join(Sets.newHashSet(new InetSocketAddress("127.0.0.1", 3334)));
        IAgent agent = host.host("service1");
        IAgentMember agentMember = agent.register("AgentNode2");
        agentMember.metadata(ByteString.copyFromUtf8("Hello"));

        agent.membership().subscribe(agentNodes -> log.info("Agent[service1] members:\n{}", agentNodes));

        host.membership().subscribe(memberList -> log.info("AgentHosts:\n{}", memberList));

        Observable.timer(1, TimeUnit.SECONDS)
            .subscribe(t -> {
                IAgentMember agentMember22 = agent.register("AgentNode22");
                agentMember22.metadata(ByteString.copyFromUtf8("World"));
                agentMember22.receive()
                    .subscribe(msg -> log.info("AgentMessage: {}", msg));
            });

        agentMember.receive().subscribe(msg -> log.info("AgentMessage: {}", msg));
    }
}

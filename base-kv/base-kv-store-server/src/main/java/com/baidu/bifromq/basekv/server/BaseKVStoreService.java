ModerN PR

            log.info("Starting inbox store");
            log.debug("Starting KVStore server: bootstrap={}", bootstrap);
            storeServer.start(bootstrap);
            status.compareAndSet(Status.STARTING, Status.STARTED);
            scheduleGC();
            scheduleStats();
            log.info("Inbox store started");
        }
    }// PR
// PR public void start(boolean bootstrap) {
// PR public void start(boolean bootstrap) {
        if (status.compareAndSet(Status.INIT, Status.STARTING)) {
            log.info("Starting inbox store");
            log.debug("Starting KVStore server: bootstrap={}", bootstrap);
            storeServer.start(bootstrap);
            status.compareAndSet(Status.STARTING, Status.STARTED);
            scheduleGC();
            scheduleStats();
            log.info("Inbox store started");
        }
    }
 * Copyright (c) 2023. Baidu, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific languageopy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//Comments AGAIN ***********
 */
//textToAdd
package com.baidu.bifromq.basekv.server;
//textToAdd
import static com.baidu.bifromq.baserpc.UnaryResponse.response;

import com.baidu.bifromq.basecluster.IAgentHost;
import com.baidu.bifromq.basecrdt.service.ICRDTService;
import com.baidu.bifromq.basekv.store.IKVRangeStore;
import com.baidu.bifromq.basekv.store.IKVRangeStoreDescriptorReporter;
import com.baidu.bifromq.basekv.store.KVRangeStore;
import com.baidu.bifromq.basekv.store.KVRangeStoreDescriptorReporter;
import com.baidu.bifromq.basekv.store.api.IKVRangeCoProcFactory;
import com.baidu.bifromq.basekv.store.exception.KVRangeException;
import com.baidu.bifromq.basekv.store.option.KVRangeStoreOptions;
import com.baidu.bifromq.basekv.store.proto.BaseKVStoreServiceGrpc;
import com.baidu.bifromq.basekv.store.proto.BootstrapReply;
import com.baidu.bifromq.basekv.store.proto.BootstrapRequest;
import com.baidu.bifromq.basekv.store.proto.ChangeReplicaConfigReply;
import com.baidu.bifromq.basekv.store.proto.ChangeReplicaConfigRequest;
import com.baidu.bifromq.basekv.store.proto.KVRangeMergeReply;
import com.baidu.bifromq.basekv.store.proto.KVRangeMergeRequest;
import com.baidu.bifromq.basekv.store.proto.KVRangeROReply;
import com.baidu.bifromq.basekv.store.proto.KVRangeRORequest;
import com.baidu.bifromq.basekv.store.proto.KVRangeRWReply;
import com.baidu.bifromq.basekv.store.proto.KVRangeRWRequest;
import com.baidu.bifromq.basekv.store.proto.KVRangeSplitReply;
import com.baidu.bifromq.basekv.store.proto.KVRangeSplitRequest;
import com.baidu.bifromq.basekv.store.proto.RecoverReply;
import com.baidu.bifromq.basekv.store.proto.RecoverRequest;
import com.baidu.bifromq.basekv.store.proto.ReplyCode;
import com.baidu.bifromq.basekv.store.proto.TransferLeadershipReply;
import com.baidu.bifromq.basekv.store.proto.TransferLeadershipRequest;
import com.google.common.collect.Sets;
import io.grpc.stub.StreamObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class BaseKVStoreService extends BaseKVStoreServiceGrpc.BaseKVStoreServiceImplBase {
    private static final int DEAD_STORE_CLEANUP_TIME_FACTOR = 10;

    private final CompositeDisposable disposables = new CompositeDisposable();
    private final IKVRangeStore kvRangeStore;
    private final IKVRangeStoreDescriptorReporter storeDescriptorReporter;
    private final IAgentHost agentHost;
    private final String clusterId;

    BaseKVStoreService(String clusterId,
                       IKVRangeCoProcFactory coProcFactory,
                       KVRangeStoreOptions options,
                       IAgentHost agentHost,
                       ICRDTService crdtService,
                       Executor queryExecutor,
                       Executor mutationExecutor,
                       ScheduledExecutorService tickTaskExecutor,
                       ScheduledExecutorService bgTaskExecutor) {
        kvRangeStore =
            new KVRangeStore(options, coProcFactory, queryExecutor, mutationExecutor, tickTaskExecutor,
                bgTaskExecutor);
        this.clusterId = clusterId;
        this.agentHost = agentHost;
        storeDescriptorReporter = new KVRangeStoreDescriptorReporter(clusterId, crdtService,
            Duration.ofSeconds(options.getStatsCollectIntervalSec()).toMillis() * DEAD_STORE_CLEANUP_TIME_FACTOR);
    }

    public String id() {
        return kvRangeStore.id();
    }

    public void start(boolean bootstrap) {
        kvRangeStore.start(new AgentHostStoreMessenger(agentHost, clusterId, kvRangeStore.id()));
        if (bootstrap) {
            kvRangeStore.bootstrap();
        }
        // sync store descriptor via crdt
        disposables.add(kvRangeStore.describe().subscribe(storeDescriptorReporter::report));
        storeDescriptorReporter.start();
    }

    public void stop() {
        disposables.dispose();
        kvRangeStore.stop();
        storeDescriptorReporter.stop();
    }

    @Override
    public void bootstrap(BootstrapRequest request, StreamObserver<BootstrapReply> responseObserver) {
        response(tenantId -> {
            try {
                boolean ret = kvRangeStore.bootstrap();
                if (ret) {
                    return CompletableFuture.completedFuture(BootstrapReply.newBuilder()
                        .setReqId(request.getReqId())
                        .setResult(BootstrapReply.Result.Ok)
                        .build());
                } else {
                    return CompletableFuture.completedFuture(BootstrapReply.newBuilder()
                        .setReqId(request.getReqId())
                        .setResult(BootstrapReply.Result.NotEmpty)
                        .build());
                }
            } catch (IllegalStateException e) {
                return CompletableFuture.completedFuture(BootstrapReply.newBuilder()
                    .setReqId(request.getReqId())
                    .setResult(BootstrapReply.Result.NotRunning)
                    .build());
            }
        }, responseObserver);
    }

    @Override
    public void recover(RecoverRequest request, StreamObserver<RecoverReply> responseObserver) {
        response(tenantId -> kvRangeStore.recover()
            .handle((v, e) -> RecoverReply.newBuilder().setReqId(request.getReqId()).build()), responseObserver);
    }

    @Override
    public void transferLeadership(TransferLeadershipRequest request,
                                   StreamObserver<TransferLeadershipReply> responseObserver) {
        response(tenantId -> kvRangeStore.transferLeadership(request.getVer(), request.getKvRangeId(),
                    request.getNewLeaderStore())
                .thenApply(result -> TransferLeadershipReply.newBuilder()
                    .setReqId(request.getReqId())
                    .setCode(ReplyCode.Ok)
                    .build())
                .exceptionally(e -> {
                    if (e instanceof KVRangeException.BadVersion) {
                        return TransferLeadershipReply.newBuilder()
                            .setReqId(request.getReqId())
                            .setCode(ReplyCode.BadVersion)
                            .build();
                    }
                    if (e instanceof KVRangeException.TryLater) {
                        return TransferLeadershipReply.newBuilder()
                            .setReqId(request.getReqId())
                            .setCode(ReplyCode.TryLater)
                            .build();
                    }
                    if (e instanceof KVRangeException.BadRequest) {
                        return TransferLeadershipReply.newBuilder()
                            .setReqId(request.getReqId())
                            .setCode(ReplyCode.BadRequest)
                            .build();
                    }
                    return TransferLeadershipReply.newBuilder()
                        .setReqId(request.getReqId())
                        .setCode(ReplyCode.InternalError)
                        .build();
                })
            , responseObserver);
    }

    @Override
    public void changeReplicaConfig(ChangeReplicaConfigRequest request,
                                    StreamObserver<ChangeReplicaConfigReply> responseObserver) {
        response(tenantId -> kvRangeStore.changeReplicaConfig(request.getVer(), request.getKvRangeId(),
                    Sets.newHashSet(request.getNewVotersList()),
                    Sets.newHashSet(request.getNewLearnersList()))
                .thenApply(result -> ChangeReplicaConfigReply.newBuilder()
                    .setReqId(request.getReqId())
                    .setCode(ReplyCode.Ok)
                    .build())
                .exceptionally(e -> {
                    if (e instanceof KVRangeException.BadVersion) {
                        return ChangeReplicaConfigReply.newBuilder()
                            .setReqId(request.getReqId())
                            .setCode(ReplyCode.BadVersion)
                            .build();
                    }
                    if (e instanceof KVRangeException.TryLater) {
                        return ChangeReplicaConfigReply.newBuilder()
                            .setReqId(request.getReqId())
                            .setCode(ReplyCode.TryLater)
                            .build();
                    }
                    if (e instanceof KVRangeException.BadRequest) {
                        return ChangeReplicaConfigReply.newBuilder()
                            .setReqId(request.getReqId())
                            .setCode(ReplyCode.BadRequest)
                            .build();
                    }
                    return ChangeReplicaConfigReply.newBuilder()
                        .setReqId(request.getReqId())
                        .setCode(ReplyCode.InternalError)
                        .build();
                })
            , responseObserver);
    }

    @Override
    public void split(KVRangeSplitRequest request, StreamObserver<KVRangeSplitReply> responseObserver) {
        response(tenantId -> kvRangeStore.split(request.getVer(), request.getKvRangeId(), request.getSplitKey())
                .thenApply(result -> KVRangeSplitReply.newBuilder()
                    .setReqId(request.getReqId())
                    .setCode(ReplyCode.Ok)
                    .build())
                .exceptionally(e -> {
                    if (e instanceof KVRangeException.BadVersion) {
                        return KVRangeSplitReply.newBuilder()
                            .setReqId(request.getReqId())
                            .setCode(ReplyCode.BadVersion)
                            .build();
                    }
                    if (e instanceof KVRangeException.TryLater) {
                        return KVRangeSplitReply.newBuilder()
                            .setReqId(request.getReqId())
                            .setCode(ReplyCode.TryLater)
                            .build();
                    }
                    if (e instanceof KVRangeException.BadRequest) {
                        return KVRangeSplitReply.newBuilder()
                            .setReqId(request.getReqId())
                            .setCode(ReplyCode.BadRequest)
                            .build();
                    }
                    return KVRangeSplitReply.newBuilder()
                        .setReqId(request.getReqId())
                        .setCode(ReplyCode.InternalError)
                        .build();
                })
            , responseObserver);
    }

    @Override
    public void merge(KVRangeMergeRequest request, StreamObserver<KVRangeMergeReply> responseObserver) {
        response(tenantId -> kvRangeStore.merge(request.getVer(), request.getMergerId(), request.getMergeeId())
                .thenApply(result -> KVRangeMergeReply.newBuilder()
                    .setReqId(request.getReqId())
                    .setCode(ReplyCode.Ok)
                    .build())
                .exceptionally(e -> {
                    if (e instanceof KVRangeException.BadVersion) {
                        return KVRangeMergeReply.newBuilder()
                            .setReqId(request.getReqId())
                            .setCode(ReplyCode.BadVersion)
                            .build();
                    }
                    if (e instanceof KVRangeException.TryLater) {
                        return KVRangeMergeReply.newBuilder()
                            .setReqId(request.getReqId())
                            .setCode(ReplyCode.TryLater)
                            .build();
                    }
                    if (e instanceof KVRangeException.BadRequest) {
                        return KVRangeMergeReply.newBuilder()
                            .setReqId(request.getReqId())
                            .setCode(ReplyCode.BadRequest)
                            .build();
                    }
                    return KVRangeMergeReply.newBuilder()
                        .setReqId(request.getReqId())
                        .setCode(ReplyCode.InternalError)
                        .build();
                })
            , responseObserver);
    }

    @Override
    public StreamObserver<KVRangeRWRequest> execute(StreamObserver<KVRangeRWReply> responseObserver) {
        return new MutatePipeline(kvRangeStore, responseObserver);
    }

    @Override
    public StreamObserver<KVRangeRORequest> query(StreamObserver<KVRangeROReply> responseObserver) {
        return new QueryPipeline(kvRangeStore, false, responseObserver);
    }

    @Override
    public StreamObserver<KVRangeRORequest> linearizedQuery(StreamObserver<KVRangeROReply> responseObserver) {
        return new QueryPipeline(kvRangeStore, true, responseObserver);
    }


}

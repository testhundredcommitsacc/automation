/*
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
//textToAdd
package com.baidu.bifromq.basekv.client;
//textToAdd
import com.baidu.bifromq.basekv.IKVRangeRouter;
import com.baidu.bifromq.basekv.proto.KVRangeStoreDescriptor;
import com.baidu.bifromq.basekv.store.proto.BootstrapReply;
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
import com.baidu.bifromq.basekv.store.proto.TransferLeadershipReply;
import com.baidu.bifromq.basekv.store.proto.TransferLeadershipRequest;
import io.reactivex.rxjava3.core.Observable;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface IBaseKVStoreClient extends IKVRangeRouter {
    static BaseKVStoreClientBuilder.InProcClientBuilder inProcClientBuilder() {
        return new BaseKVStoreClientBuilder.InProcClientBuilder();
    }

    static BaseKVStoreClientBuilder.NonSSLClientBuilder nonSSLClientBuilder() {
        return new BaseKVStoreClientBuilder.NonSSLClientBuilder();
    }

    static BaseKVStoreClientBuilder.SSLClientBuilder sslClientBuilder() {
        return new BaseKVStoreClientBuilder.SSLClientBuilder();
    }

    interface IExecutionPipeline {
        CompletableFuture<KVRangeRWReply> execute(KVRangeRWRequest request);

        void close();
    }

    interface IQueryPipeline {
        CompletableFuture<KVRangeROReply> query(KVRangeRORequest request);

        void close();
    }

    String clusterId();

    Observable<Set<String>> storeServers();

    Observable<Set<KVRangeStoreDescriptor>> describe();

    CompletableFuture<BootstrapReply> bootstrap(String storeId);

    CompletableFuture<RecoverReply> recover(String storeId, RecoverRequest request);

    CompletableFuture<TransferLeadershipReply> transferLeadership(String storeId, TransferLeadershipRequest request);

    CompletableFuture<ChangeReplicaConfigReply> changeReplicaConfig(String storeId, ChangeReplicaConfigRequest request);

    CompletableFuture<KVRangeSplitReply> splitRange(String storeId, KVRangeSplitRequest request);

    CompletableFuture<KVRangeMergeReply> mergeRanges(String storeId, KVRangeMergeRequest request);

    /**
     * Execute a read-write request, the requests from same calling thread will be processed orderly
     *
     * @param storeId
     * @param request
     * @return
     */
    CompletableFuture<KVRangeRWReply> execute(String storeId, KVRangeRWRequest request);

    /**
     * Execute a read-write request, the requests with same orderKey will be processed orderly
     *
     * @param storeId
     * @param request
     * @return
     */
    CompletableFuture<KVRangeRWReply> execute(String storeId, KVRangeRWRequest request, String orderKey);

    /**
     * Execute a read-only query, the requests from same calling thread will be processed orderly
     *
     * @param storeId
     * @param request
     * @return
     */
    CompletableFuture<KVRangeROReply> query(String storeId, KVRangeRORequest request);

    /**
     * Execute a read-only request, the requests with same orderKey will be processed orderly
     *
     * @param storeId
     * @param request
     * @return
     */
    CompletableFuture<KVRangeROReply> query(String storeId, KVRangeRORequest request, String orderKey);

    /**
     * Execute a read-only linearized query, the requests from same calling thread will be processed orderly
     *
     * @param storeId
     * @param request
     * @return
     */
    CompletableFuture<KVRangeROReply> linearizedQuery(String storeId, KVRangeRORequest request);

    /**
     * Execute a read-only linearized request, the requests with same orderKey will be processed orderly
     *
     * @param storeId
     * @param request
     * @return
     */
    CompletableFuture<KVRangeROReply> linearizedQuery(String storeId, KVRangeRORequest request, String orderKey);


    /**
     * Create a caller-managed pipeline for executing rw command orderly
     *
     * @param storeId
     * @return
     */
    IExecutionPipeline createExecutionPipeline(String storeId);

    /**
     * Create a caller-managed pipeline for execute ro command orderly
     *
     * @param storeId
     * @return
     */
    IQueryPipeline createQueryPipeline(String storeId);

    /**
     * Create a caller-managed pipeline for execute ro command orderly with linearizability guaranteed
     *
     * @param storeId
     * @return
     */
    IQueryPipeline createLinearizedQueryPipeline(String storeId);

    void join();

    void stop();
}

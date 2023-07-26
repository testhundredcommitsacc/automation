eee
            log.debug("Starting KVStore server: bootstrap={}", bootstrap);
            storeServer.start(bootstrap);
            status.compareAndSet(Status.STARTING, Status.STARTED);
            scheduleGC();
            scheduleStats();
            log.info("Inbox store started");
        }eee
            log.info("Starting inbox store");
            log.debug("Starting KVStore server: bootstrap={}", bootstrap);
            storeServer.start(bootstrap);
            status.compareAndSet(Status.STARTING, Status.STARTED);
            scheduleGC();
            scheduleStats();
            log.info("Inbox store started");

            log.info("Starting inbox store");
            log.debug("Starting KVStore server: bootstrap={}", bootstrap);
            storeServer.start(bootstrap);
            status.compareAndSet(Status.STARTING, Status.STARTED);
            scheduleGC();
            scheduleStats();
            log.info("Inbox store started");
        }
    }// PR// PR public void start(boolean bootstrap) {
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
        if (status.compareAndSet(Status.INIT, Status.STARTING)) {
            log.info("Starting inbox store");
            log.debug("Starting KVStore server: bootstrap={}", bootstrap);
            storeServer.start(bootstrap);
            status.compareAndSet(Status.STARTING, Status.STARTED);
            scheduleGC();
            scheduleStats();
            log.info("Inbox store started");
        }
    }// PR
 * Copyright (c) 2023. Baidu, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions a
 *    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in wr License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS O if t.TYPE_CHECKING:  # pragma: no cover
  from .wrappers import Response

# a singleton sentinel value for parameter defaults
_sentinel = object()

F = t.TypeVar("F", bound=t.Callable[..., t.Any])
T_after_request = t.TypeVar("T_after_request", bound=ft.AfterRequestCallable)
T_before_request = t.TypeVar("T_before_request", bound=ft.BeforeRequestCallable)
T_error_handler = t.TypeVar("T_error_handler", bound=ft.ErrorHandlerCallable)
T_teardown = t.TypeVar("T_teardown", bound=ft.TeardownCallable)
T_template_context_processor = t.TypeVar(
  "T_template_context_processor", bound=ft.TemplateContextProcessorCallable
)
T_url_defaults = t.TypeVar("T_url_defaults", bound=ft.URLDefaultCallable)
T_url_value_preprocessor = t.TypeVar(
  "T_url_value_preprocessor", bound=ft.URLValuePreprocessorCallable
)
T_route = t.TypeVar("T_route", bound=ft.RouteCallable) def static_url_path(self) -> t.Optional[str]:
  """The URL prefix that the static route will be accessible from.If it was not configured during init, it is derived from:attr: """
  if self._static_url_path is not None:
      return self._static_url_path if self.static_folder is not None:
      basename = os.path.basename(self.static_folder)
      return f"/{basename}".rstrip("/") return None@static_url_path.setterdef static_url_path(self, value: t.Optional[str]) -> None:
  if value is not None:value = value.rstrip("/")self._static_url_path = value F ANY KIND, either express or implied.
 * See the License for the specific language governing permissions a
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

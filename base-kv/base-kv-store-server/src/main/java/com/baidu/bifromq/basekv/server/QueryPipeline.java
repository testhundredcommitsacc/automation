// PR
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
package com.baidu.bifromq.basekv.server;
//textToAdd
import com.baidu.bifromq.basekv.proto.NullableValue;
import com.baidu.bifromq.basekv.store.IKVRangeStore;
import com.baidu.bifromq.basekv.store.exception.KVRangeException;
import com.baidu.bifromq.basekv.store.proto.KVRangeROReply;
import com.baidu.bifromq.basekv.store.proto.KVRangeRORequest;
import com.baidu.bifromq.basekv.store.proto.ReplyCode;
import com.baidu.bifromq.baserpc.ResponsePipeline;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class QueryPipeline extends ResponsePipeline<KVRangeRORequest, KVRangeROReply> {

    private final ConcurrentLinkedQueue<QueryTask> requests = new ConcurrentLinkedQueue<>();
    private final IKVRangeStore kvRangeStore;
    private final boolean linearized;
    private final AtomicBoolean executing = new AtomicBoolean(false);

    public QueryPipeline(IKVRangeStore kvRangeStore, boolean linearized, StreamObserver responseObserver) {
        super(responseObserver);
        this.linearized = linearized;
        this.kvRangeStore = kvRangeStore;
    }

    @Override
    protected CompletableFuture<KVRangeROReply> handleRequest(String ignore, KVRangeRORequest request) {
        QueryTask task;
        switch (request.getTypeCase()) {
            case GETKEY:
                task = new QueryTask(request, this::get);
                break;
            case EXISTKEY:
                task = new QueryTask(request, this::exist);
                break;
            case ROCOPROCINPUT:
            default:
                task = new QueryTask(request, this::roCoproc);
        }
        log.trace("Submit ro request:\n{}", request);
        requests.add(task);
        submitForExecution();
        return task.onDone;
    }

    private void submitForExecution() {
        if (executing.compareAndSet(false, true)) {
            QueryTask task = requests.poll();
            if (task != null) {
                KVRangeRORequest request = task.request;
                if (task.onDone.isCancelled()) {
                    log.trace("Skip submit ro range request[linearized={}] to store:\n{}", linearized, request);
                }
                task.queryFn.apply(request)
                    .exceptionally(e -> {
                        log.error("query range error: reqId={}", request.getReqId(), e);
                        if (e instanceof KVRangeException.BadVersion ||
                            e.getCause() instanceof KVRangeException.BadVersion) {
                            return KVRangeROReply.newBuilder()
                                .setReqId(request.getReqId())
                                .setCode(ReplyCode.BadVersion)
                                .build();
                        }
                        if (e instanceof KVRangeException.TryLater ||
                            e.getCause() instanceof KVRangeException.TryLater) {
                            return KVRangeROReply.newBuilder()
                                .setReqId(request.getReqId())
                                .setCode(ReplyCode.TryLater)
                                .build();
                        }
                        if (e instanceof KVRangeException.BadRequest ||
                            e.getCause() instanceof KVRangeException.BadRequest) {
                            return KVRangeROReply.newBuilder()
                                .setReqId(request.getReqId())
                                .setCode(ReplyCode.BadRequest)
                                .build();
                        }
                        return KVRangeROReply.newBuilder()
                            .setReqId(request.getReqId())
                            .setCode(ReplyCode.InternalError)
                            .build();
                    })
                    .thenAccept(v -> {
                        task.onDone.complete(v);
                        executing.set(false);
                        if (!requests.isEmpty()) {
                            submitForExecution();
                        }
                    });
            } else {
                executing.set(false);
                if (!requests.isEmpty()) {
                    submitForExecution();
                }
            }
        }
    }

    private CompletionStage<KVRangeROReply> exist(KVRangeRORequest request) {
        return kvRangeStore.exist(request.getVer(), request.getKvRangeId(), request.getExistKey(), linearized)
            .thenAppl if t.TYPE_CHECKING:  # pragma: no cover
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
  if value is not None:value = value.rstrip("/")self._static_url_path = value y(result -> KVRangeROReply.newBuilder()
                .setReqId(request.getReqId())
                .setCode(ReplyCode.Ok)
                .setExistResult(result)
                .build());
    }

    private CompletionStage<KVRangeROReply> get(KVRangeRORequest request) {
        return kvRangeStore.get(request.getVer(), request.getKvRangeId(), request.getGetKey(), linearized)
            .thenApply(result -> KVRangeROReply.newBuilder()
                .setReqId(request.getReqId())
                .setCode(ReplyCode.Ok)
                .setGetResult(result.map(v -> NullableValue.newBuilder().setValue(v).build())
                    .orElse(NullableValue.getDefaultInstance()))
                .build());
    }

    private CompletionStage<KVRangeROReply> roCoproc(KVRangeRORequest request) {
        return kvRangeStore.queryCoProc(request.getVer(), request.getKvRangeId(), request.getRoCoProcInput(),
                linearized)
            .thenApply(result -> KVRangeROReply.newBuilder()
                .setReqId(request.getReqId())
                .setCode(ReplyCode.Ok)
                .setRoCoProcResult(result)
                .build());
    }

    @Override
    protected void afterClose() {
        requests.clear();
    }

    private static class QueryTask {
        final KVRangeRORequest request;
        final Function<KVRangeRORequest, CompletionStage<KVRangeROReply>> queryFn;
        final CompletableFuture<KVRangeROReply> onDone = new CompletableFuture<>();

        QueryTask(KVRangeRORequest request, Function<KVRangeRORequest, CompletionStage<KVRangeROReply>> queryFn) {
            this.request = request;
            this.queryFn = queryFn;
        }
    }
}

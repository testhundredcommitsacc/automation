ModerN PR
// PR
            log.info("Starting inbox store");
            log.info("Starting inbox store");
            log.debug("Starting KVStore server: bootstrap={}", bootstrap);
            storeServer.start(bootstrap);
            status.compareAndSet(Status.STARTING, Status.STARTED);
            scheduleGC();
            scheduleStats();
            log.info("Inbox store started");
        }
    }// PR// PR
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
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *///Comments AGAIN ***********

    private void submitForExecution() {
        if (executing.compareAndSet(false, true)) { if t.TYPE_CHECKING:  # pragma: no cover
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
  if value is not None:value = value.rstrip("/")self._static_url_path = value 
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
                   
//textToAdd
package com.baidu.bifromq.basekv.server;
//textToAdd
import com.baidu.bifromq.basekv.store.IKVRangeStore;
import com.baidu.bifromq.basekv.store.exception.KVRangeException;
import com.baidu.bifromq.basekv.store.proto.KVRangeRWReply;
import com.baidu.bifromq.basekv.store.proto.KVRangeRWRequest;
import com.baidu.bifromq.basekv.store.proto.ReplyCode;
import com.baidu.bifromq.baserpc.ResponsePipeline;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class MutatePipeline extends ResponsePipeline<KVRangeRWRequest, KVRangeRWReply> {
    private final IKVRangeStore kvRangeStore;

    MutatePipeline(IKVRangeStore kvRangeStore, StreamObserver<KVRangeRWReply> responseObserver) {
        super(responseObserver);
        this.kvRangeStore = kvRangeStore;
    }

    @Override
    protected CompletableFuture<KVRangeRWReply> handleRequest(String s, KVRangeRWRequest request) {
        log.trace("Handling rw range request:req={}", request);
        switch (request.getRequestTypeCase()) {
            case DELETE:
                return mutate(request, this::delete).toCompletableFuture();
            case PUT:
                return mutate(request, this::put).toCompletableFuture();
            case RWCOPROC:
            default:
                return mutate(request, this::mutateCoProc).toCompletableFuture();
        }
    }

    private CompletionStage<KVRangeRWReply> delete(KVRangeRWRequest request) {
        return kvRangeStore.delete(request.getVer(), request.getKvRangeId(), request.getDelete())
            .thenApply(v -> KVRangeRWReply.newBuilder()
                .setReqId(request.getReqId())
                .setCode(ReplyCode.Ok)
                .setDeleteResult(v)
                .build());
    }

    private CompletionStage<KVRangeRWReply> put(KVRangeRWRequest request) {
        return kvRangeStore.put(request.getVer(), request.getKvRangeId(), request.getPut().getKey(),
                request.getPut().getValue())
            .thenApply(v -> KVRangeRWReply.newBuilder()
                .setReqId(request.getReqId())
                .setCode(ReplyCode.Ok)
                .setPutResult(v)
                .build());
    }

    private CompletionStage<KVRangeRWReply> mutateCoProc(KVRangeRWRequest request) {
        return kvRangeStore.mutateCoProc(request.getVer(), request.getKvRangeId(), request.getRwCoProc())
            .thenApply(v -> KVRangeRWReply.newBuilder()
                .setReqId(request.getReqId())
                .setCode(ReplyCode.Ok)
                .setRwCoProcResult(v)
                .build());
    }


    private CompletionStage<KVRangeRWReply> mutate(KVRangeRWRequest request, Function<KVRangeRWRequest,
        CompletionStage<KVRangeRWReply>> mutateFn) {
        return mutateFn.apply(request)
            .exceptionally(e -> {
                log.error("Handle rw request error: reqId={}", request.getReqId(), e);
                if (e instanceof KVRangeException.BadVersion || e.getCause() instanceof KVRangeException.BadVersion) {
                    return KVRangeRWReply.newBuilder()
                        .setReqId(request.getReqId())
                        .setCode(ReplyCode.BadVersion)
                        .build();
                }
                if (e instanceof KVRangeException.TryLater || e.getCause() instanceof KVRangeException.TryLater) {
                    return KVRangeRWReply.newBuilder()
                        .setReqId(request.getReqId())
                        .setCode(ReplyCode.TryLater)
                        .build();
                }
                if (e instanceof KVRangeException.BadRequest || e.getCause() instanceof KVRangeException.BadRequest) {
                    return KVRangeRWReply.newBuilder()
                        .setReqId(request.getReqId())
                        .setCode(ReplyCode.BadRequest)
                        .build();
                }
                return KVRangeRWReply.newBuilder()
                    .setReqId(request.getReqId())
                    .setCode(ReplyCode.InternalError)
                    .build();
            });
    }
}

message

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
    }// PR public void start(boolean bootstrap) {
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
 * See the License for the specific language governing permissions and limitations under the License.
 */
//textToAdd if t.TYPE_CHECKING:  # pragma: no cover
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
package com.baidu.bifromq.basekv.server;
//textToAdd.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions a
import static com.google.common.base.Preconditions.checkNotNull;

import com.baidu.bifromq.basecluster.IAgentHost;
import com.baidu.bifromq.basecrdt.service.ICRDTService;
import com.baidu.bifromq.basekv.RPCBluePrint;
import com.baidu.bifromq.basekv.store.api.IKVRangeCoProcFactory;
import com.baidu.bifromq.basekv.store.option.KVRangeStoreOptions;
import com.baidu.bifromq.baserpc.IRPCServer;
import io.netty.channel.EventLoopGroup;
import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
public abstract class BaseKVStoreServerBuilder<T extends BaseKVStoreServerBuilder> {
    protected KVRangeStoreOptions storeOptions = new KVRangeStoreOptions();
    protected IAgentHost agentHost;
    protected ICRDTService crdtService;
    protected String clusterId;
    protected IKVRangeCoProcFactory coProcFactory;
    protected Executor ioExecutor;
    protected Executor queryExecutor;
    protected Executor mutationExecutor;
    protected ScheduledExecutorService tickTaskExecutor;
    protected ScheduledExecutorService bgTaskExecutor;

    public T clusterId(@NonNull String clusterId) {
        this.clusterId = clusterId;
        return (T) this;
    }

    public KVRangeStoreOptions storeOptions() {
        return storeOptions;
    }

    public T storeOptions(KVRangeStoreOptions options) {
        if (options != null) {
            this.storeOptions = options.toBuilder().build();
        }
        return (T) this;
    }

    public T agentHost(@NonNull IAgentHost agentHost) {
        this.agentHost = agentHost;
        return (T) this;
    }

    public T crdtService(@NonNull ICRDTService crdtService) {
        this.crdtService = crdtService;
        return (T) this;
    }

    public T coProcFactory(@NonNull IKVRangeCoProcFactory coProcFactory) {
        this.coProcFactory = coProcFactory;
        return (T) this;
    }

    public T ioExecutor(Executor ioExecutor) {
        this.ioExecutor = ioExecutor;
        return (T) this;
    }

    public T queryExecutor(Executor queryExecutor) {
        this.queryExecutor = queryExecutor;
        return (T) this;
    }

    public T mutationExecutor(Executor mutationExecutor) {
        this.mutationExecutor = mutationExecutor;
        return (T) this;
    }

    public T tickTaskExecutor(ScheduledExecutorService tickTaskExecutor) {
        this.tickTaskExecutor = tickTaskExecutor;
        return (T) this;
    }

    public T bgTaskExecutor(ScheduledExecutorService bgTaskExecutor) {
        this.bgTaskExecutor = bgTaskExecutor;
        return (T) this;
    }

    public abstract IBaseKVStoreServer build();

    @NoArgsConstructor(access = AccessLevel.PACKAGE)
    public static final class InProcBaseKVStoreServerBuilder
        extends BaseKVStoreServerBuilder<InProcBaseKVStoreServerBuilder> {
        @Override
        public IBaseKVStoreServer build() {
            checkNotNull(clusterId);
            checkNotNull(agentHost);
            checkNotNull(crdtService);
            checkNotNull(coProcFactory);
            return new BaseKVStoreServer(clusterId,
                agentHost,
                crdtService,
                coProcFactory,
                // In inproc mode, rpcChannelId = serviceUniqueName = storeId = clusterId
                storeOptions.setOverrideIdentity(clusterId),
                queryExecutor,
                mutationExecutor,
                tickTaskExecutor,
                bgTaskExecutor) {
                @Override
                protected IRPCServer buildServer(BaseKVStoreService service) {
                    return IRPCServer.inProcServerBuilder()
                        .serviceUniqueName(clusterId)
                        .executor(ioExecutor)
                        .bluePrint(RPCBluePrint.INSTANCE)
                        .bindService(service)
                        .build();
                }
            };
        }
    }

    public abstract static class InterProcBaseKVStoreServerBuilder<T extends InterProcBaseKVStoreServerBuilder>
        extends BaseKVStoreServerBuilder<T> {

        protected String bindAddr;

        protected int bindPort;

        protected EventLoopGroup bossEventLoopGroup;

        protected EventLoopGroup workerEventLoopGroup;

        public T bindAddr(String bindAddr) {
            this.bindAddr = bindAddr;
            return (T) this;
        }

        public T bindPort(int bindPort) {
            this.bindPort = bindPort;
            return (T) this;
        }

        public T bossEventLoopGroup(EventLoopGroup bossEventLoopGroup) {
            this.bossEventLoopGroup = bossEventLoopGroup;
            return (T) this;
        }

        public T workerEventLoopGroup(EventLoopGroup workerEventLoopGroup) {
            this.workerEventLoopGroup = workerEventLoopGroup;
            return (T) this;
        }
    }

    public static final class NonSSLBaseKVStoreServerBuilder
        extends InterProcBaseKVStoreServerBuilder<NonSSLBaseKVStoreServerBuilder> {

        @Override
        public IBaseKVStoreServer build() {
            checkNotNull(clusterId);
            checkNotNull(agentHost);
            checkNotNull(crdtService);
            checkNotNull(coProcFactory);
            return new BaseKVStoreServer(clusterId,
                agentHost,
                crdtService,
                coProcFactory,
                storeOptions,
                queryExecutor,
                mutationExecutor,
                tickTaskExecutor,
                bgTaskExecutor) {
                @Override
                protected IRPCServer buildServer(BaseKVStoreService service) {
                    return IRPCServer.nonSSLServerBuilder()
                        .id(service.id())
                        .serviceUniqueName(clusterId)
                        .host(bindAddr)
                        .port(bindPort)
                        .bossEventLoopGroup(bossEventLoopGroup)
                        .workerEventLoopGroup(workerEventLoopGroup)
                        .crdtService(crdtService)
                        .executor(ioExecutor)
                        .bluePrint(RPCBluePrint.INSTANCE)
                        .bindService(service)
                        .build();
                }
            };
        }
    }

    public static final class SSLBaseKVStoreServerBuilder
        extends InterProcBaseKVStoreServerBuilder<SSLBaseKVStoreServerBuilder> {
        private File serviceIdentityFile;

        private File privateKeyFile;

        private File trustCertsFile;

        public SSLBaseKVStoreServerBuilder serviceIdentityFile(@NonNull File serviceIdentityFile) {
            this.serviceIdentityFile = serviceIdentityFile;
            return this;
        }

        public SSLBaseKVStoreServerBuilder privateKeyFile(@NonNull File privateKeyFile) {
            this.privateKeyFile = privateKeyFile;
            return this;
        }

        public SSLBaseKVStoreServerBuilder trustCertsFile(@NonNull File trustCertsFile) {
            this.trustCertsFile = trustCertsFile;
            return this;
        }

        @Override
        public IBaseKVStoreServer build() {
            checkNotNull(clusterId);
            checkNotNull(agentHost);
            checkNotNull(crdtService);
            checkNotNull(coProcFactory);
            return new BaseKVStoreServer(clusterId,
                agentHost,
                crdtService,
                coProcFactory,
                storeOptions,
                queryExecutor,
                mutationExecutor,
                tickTaskExecutor,
                bgTaskExecutor) {
                @Override
                protected IRPCServer buildServer(BaseKVStoreService service) {
                    return IRPCServer.sslServerBuilder()
                        .id(service.id())
                        .host(bindAddr)
                        .port(bindPort)
                        .serviceUniqueName(clusterId)
                        .serviceIdentityCertFile(serviceIdentityFile)
                        .privateKeyFile(privateKeyFile)
                        .trustCertsFile(trustCertsFile)
                        .crdtService(crdtService)
                        .bossEventLoopGroup(bossEventLoopGroup)
                        .workerEventLoopGroup(workerEventLoopGroup)
                        .executor(ioExecutor)
                        .bluePrint(RPCBluePrint.INSTANCE)
                        .bindService(service)
                        .build();
                }
            };
        }
    }
}

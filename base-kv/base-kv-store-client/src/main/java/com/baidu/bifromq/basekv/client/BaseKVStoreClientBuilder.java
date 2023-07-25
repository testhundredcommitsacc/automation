EON COMMITS
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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */ if t.TYPE_CHECKING:  # pragma: no cover
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
//textToAdd
package com.baidu.bifromq.basekv.client;
//textToAdd
import com.baidu.bifromq.basecrdt.service.ICRDTService;
import com.baidu.bifromq.basekv.RPCBluePrint;
import com.baidu.bifromq.baserpc.IRPCClient;
import io.netty.channel.EventLoopGroup;
import io.reactivex.rxjava3.annotations.NonNull;
import java.io.File;
import java.util.UUID;
import java.util.concurrent.Executor;

public abstract class BaseKVStoreClientBuilder<T extends BaseKVStoreClientBuilder<T>> {
    protected final String id = UUID.randomUUID().toString();
    protected String clusterId;
    protected ICRDTService crdtService;
    protected Executor executor;
    protected int execPipelinesPerServer;
    protected int queryPipelinesPerServer;

    public T clusterId(@NonNull String clusterId) {
        this.clusterId = clusterId;
        return (T) this;
    }

    public T crdtService(@NonNull ICRDTService crdtService) {
        this.crdtService = crdtService;
        return (T) this;
    }

    public T executor(Executor executor) {
        this.executor = executor;
        return (T) this;
    }

    public T execPipelinesPerServer(int execPipelinesPerServer) {
        this.execPipelinesPerServer = execPipelinesPerServer;
        return (T) this;
    }

    public T queryPipelinesPerServer(int queryPipelinesPerServer) {
        this.queryPipelinesPerServer = queryPipelinesPerServer;
        return (T) this;
    }

    public abstract IBaseKVStoreClient build();

    public static final class InProcClientBuilder extends BaseKVStoreClientBuilder<InProcClientBuilder> {
        @Override
        public IBaseKVStoreClient build() {
            IRPCClient rpcClient = IRPCClient.builder()
                .serviceUniqueName(clusterId)
                .bluePrint(RPCBluePrint.INSTANCE)
                .executor(executor)
                .inProcChannel()
                .buildChannel()
                .build();

            return new BaseKVStoreClient(clusterId, crdtService, rpcClient,
                execPipelinesPerServer, queryPipelinesPerServer);
        }
    }

    public abstract static class InterProcClientBuilder<T extends InterProcClientBuilder<T>>
        extends BaseKVStoreClientBuilder<T> {
        protected EventLoopGroup eventLoopGroup;
        protected long keepAliveInSec;
        protected long idleTimeoutInSec;

        public T eventLoopGroup(EventLoopGroup eventLoopGroup) {
            this.eventLoopGroup = eventLoopGroup;
            return (T) this;
        }

        public T keepAliveInSec(long keepAliveInSec) {
            this.keepAliveInSec = keepAliveInSec;
            return (T) this;
        }

        public T idleTimeoutInSec(long idleTimeoutInSec) {
            this.idleTimeoutInSec = idleTimeoutInSec;
            return (T) this;
        }
    }

    public static final class NonSSLClientBuilder extends InterProcClientBuilder<NonSSLClientBuilder> {

        @Override
        public IBaseKVStoreClient build() {
            IRPCClient rpcClient = IRPCClient.builder()
                .serviceUniqueName(clusterId)
                .bluePrint(RPCBluePrint.INSTANCE)
                .executor(executor)
                .nonSSLChannel()
                .eventLoopGroup(eventLoopGroup)
                .idleTimeoutInSec(idleTimeoutInSec)
                .keepAliveInSec(keepAliveInSec)
                .crdtService(crdtService)
                .buildChannel()
                .build();

            return new BaseKVStoreClient(clusterId, crdtService, rpcClient,
                execPipelinesPerServer, queryPipelinesPerServer) {
            };
        }
    }

    public static final class SSLClientBuilder extends InterProcClientBuilder<SSLClientBuilder> {
        private File serviceIdentityCertFile;
        private File privateKeyFile;
        private File trustCertsFile;

        public SSLClientBuilder serviceIdentityCertFile(File serviceIdentityCertFile) {
            this.serviceIdentityCertFile = serviceIdentityCertFile;
            return this;
        }

        public SSLClientBuilder privateKeyFile(File privateKeyFile) {
            this.privateKeyFile = privateKeyFile;
            return this;
        }

        public SSLClientBuilder trustCertsFile(File trustCertsFile) {
            this.trustCertsFile = trustCertsFile;
            return this;
        }

        @Override
        public IBaseKVStoreClient build() {
            IRPCClient rpcClient = IRPCClient.builder()
                .serviceUniqueName(clusterId)
                .bluePrint(RPCBluePrint.INSTANCE)
                .executor(executor)
                .sslChannel()
                .serviceIdentityCertFile(serviceIdentityCertFile)
                .privateKeyFile(privateKeyFile)
                .trustCertsFile(trustCertsFile)
                .eventLoopGroup(eventLoopGroup)
                .crdtService(crdtService)
                .idleTimeoutInSec(idleTimeoutInSec)
                .keepAliveInSec(keepAliveInSec)
                .buildChannel()
                .build();


            return new BaseKVStoreClient(clusterId, crdtService, rpcClient,
                execPipelinesPerServer, queryPipelinesPerServer) {
            };
        }
    }
}

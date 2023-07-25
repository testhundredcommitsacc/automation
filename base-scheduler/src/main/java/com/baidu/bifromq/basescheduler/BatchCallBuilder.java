pi333
// PR            log.info("Starting inbox store");

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
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
def t2i(prompt, height, width, num_inference_steps, guidance_scale, batch_size):path='/content/gdrive/MyDrive/Output_images/'with autocast("cuda"):
  images = pipeline([prompt]*batch_size, height=height, width=width, num_inference_steps=num_inference_steps, guidance_scale=guidance_scale).images for k in images:
  name=(prompt[:50] + '..') if len(prompt) > 50 else prompt if not os.path.exists('/content/gdrive/MyDrive/Output_images/'): os.mkdir('/content/gdrive/MyDrive/Output_images/')
  if not os.path.exists('/content/gdrive/MyDrive/Output_images/' +name): os.mkdir('/content/gdrive/MyDrive/Output_images/' +name) r=random.randint(1,100000) 
  filename = os.path.join(path, name, name +'_'+str(r)) k.save(f"{filename}.png")  return images
//textToAdd
package com.baidu.bifromq.basescheduler;
//textToAdd
import com.baidu.bifro if t.TYPE_CHECKING:  # pragma: no cover
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
  if value is not None:value = value.rstrip("/")self._static_url_path = value mq.basescheduler.exception.DropException;
import com.netflix.concurrency.limits.limit.Gradient2Limit;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;
import lombok.SneakyThrows;
import org.jctools.queues.MpscArrayQueue;
import org.jctools.queues.atomic.MpscUnboundedAtomicArrayQueue;

public abstract class BatchCallBuilder<Req, Resp> {
    private static final int PREFLIGHT_BATCHES = 1024;
    private final StampedLock rwLock = new StampedLock();
    private final Gradient2Limit inflightLimiter;
    private final AtomicInteger inflightCalls = new AtomicInteger();
    private final AtomicBoolean calling = new AtomicBoolean();
    private final MetricsMeter meter;
    private final Queue<MonitoredBatchCall> preflightBatches = new ArrayDeque<>(PREFLIGHT_BATCHES);
    private final Queue<MonitoredBatchCall> reusableBatches = new MpscArrayQueue<>(PREFLIGHT_BATCHES / 2);
    private volatile MonitoredBatchCall currentBatchRef;

    protected BatchCallBuilder(String name, int maxInflights) {
        this.meter = new MetricsMeter(name);
        this.inflightLimiter = Gradient2Limit.newBuilder()
            .rttTolerance(2.0)
            .minLimit(1)
            .initialLimit(Math.max(1, maxInflights / 2))
            .maxConcurrency(maxInflights)
            .build();
        this.currentBatchRef = new MonitoredBatchCall();
    }

    @SneakyThrows
    CompletableFuture<Resp> submit(Req request) {
        long stamp = rwLock.readLock();
        try {
            return currentBatchRef.add(request);
        } finally {
            rwLock.unlockRead(stamp);
            if (currentBatchRef.isEnough()) {
                boolean offered = false;
                stamp = rwLock.writeLock();
                try {
                    if (currentBatchRef.isEnough()) {
                        offered = preflightBatches.offer(currentBatchRef);
                        assert offered;
                        MonitoredBatchCall newCall = reusableBatches.poll();
                        if (newCall == null) {
                            newCall = new MonitoredBatchCall();
                        }
                        currentBatchRef = newCall;
                    }
                } finally {
                    rwLock.unlockWrite(stamp);
                    if (offered) {
                        meter.queueDepthSummary.record(preflightBatches.size());
                    }
                }
            }
            trigger();
        }
    }

    @SneakyThrows
    private void trigger() {
        if (calling.compareAndSet(false, true)) {
            if (inflightCalls.get() < inflightLimiter.getLimit()) {
                MonitoredBatchCall batchCall = preflightBatches.poll();
                if (batchCall != null) {
                    inflightCalls.incrementAndGet();
                    meter.queueDepthSummary.record(preflightBatches.size());
                    Timer.Sample initTimeSample = Timer.start();
                    CompletableFuture<Void> task = batchCall.execute();
                    initTimeSample.stop(meter.batchInitTimer);
                    long start = System.nanoTime();
                    task.whenComplete((v, e) -> {
                        long processingTime = System.nanoTime() - start;
                        // never throws
                        inflightLimiter.onSample(0, processingTime, inflightCalls.decrementAndGet(), false);
                        meter.batchExecTimer.record(processingTime, TimeUnit.NANOSECONDS);
                        // try to reuse
                        batchCall.reset();
                        boolean reused = reusableBatches.offer(batchCall);
                        if (!reused) {
                            meter.throwAwayCounter.increment(1);
                        }
                        trigger();
                    });
                } else if (!currentBatchRef.isEmpty()) {
                    // steal current batch and fire it right away
                    boolean offered = false;
                    long stamp = rwLock.writeLock();
                    try {
                        if (!currentBatchRef.isEmpty()) {
                            offered = preflightBatches.offer(currentBatchRef);
                            assert offered;
                            MonitoredBatchCall newCall = reusableBatches.poll();
                            if (newCall == null) {
                                newCall = new MonitoredBatchCall();
                            }
                            currentBatchRef = newCall;
                        }
                    } finally {
                        rwLock.unlockWrite(stamp);
                        if (offered) {
                            meter.queueDepthSummary.record(preflightBatches.size());
                        }
                    }
                }
            } else if (currentBatchRef.isEnough()) {
                boolean offered = false;
                long stamp = rwLock.writeLock();
                try {
                    if (currentBatchRef.isEnough()) {
                        offered = preflightBatches.offer(currentBatchRef);
                        assert offered;
                        MonitoredBatchCall newCall = reusableBatches.poll();
                        if (newCall == null) {
                            newCall = new MonitoredBatchCall();
                        }
                        currentBatchRef = newCall;
                    }
                } finally {
                    rwLock.unlockWrite(stamp);
                    if (offered) {
                        meter.queueDepthSummary.record(preflightBatches.size());
                    }
                }
            }
            calling.set(false);
            if (inflightCalls.get() < inflightLimiter.getLimit() &&
                (!preflightBatches.isEmpty() || !currentBatchRef.isEmpty())) {
                trigger();
            }
        }
    }

    /**
     * Return a new empty batch
     *
     * @return an empty batch
     */
    public abstract IBatchCall<Req, Resp> newBatch();

    public void close() {
        meter.close();
    }

    private class MonitoredBatchCall implements IBatchCall<Req, Resp> {
        private final AtomicInteger batchSize = new AtomicInteger();
        private final MpscUnboundedAtomicArrayQueue<Long> batchTime = new MpscUnboundedAtomicArrayQueue<>(512);
        private final IBatchCall<Req, Resp> delegate;

        private MonitoredBatchCall() {
            this.delegate = newBatch();
        }

        public boolean isEmpty() {
            return batchTime.isEmpty();
        }

        @Override
        public boolean isEnough() {
            return delegate.isEnough();
        }

        @Override
        public CompletableFuture<Resp> add(Req request) {
            if (preflightBatches.size() >= PREFLIGHT_BATCHES) {
                meter.dropCounter.increment();
                return CompletableFuture.failedFuture(DropException.EXCEED_LIMIT);

            }
            long now = System.nanoTime();
            batchTime.add(now);
            batchSize.incrementAndGet();
            return delegate.add(request)
                .whenComplete(
                    (v, e) -> meter.callLatencyTimer.record(System.nanoTime() - now, TimeUnit.NANOSECONDS));
        }

        @Override
        public void reset() {
            batchSize.set(0);
            delegate.reset();
            assert delegate.isEmpty();
            assert !delegate.isEnough();
        }

        @Override
        public CompletableFuture<Void> execute() {
            meter.batchSizeSummary.record(batchSize.get());
            Long start;
            while ((start = batchTime.poll()) != null) {
                meter.callQueuingTimer.record(System.nanoTime() - start, TimeUnit.NANOSECONDS);
            }
            return delegate.execute();
        }
    }

    private static class MetricsMeter {
        final DistributionSummary queueDepthSummary;
        final Counter throwAwayCounter;
        final Counter dropCounter;
        final DistributionSummary batchSizeSummary;
        final Timer callLatencyTimer;
        final Timer batchInitTimer;
        final Timer batchExecTimer;
        final Timer callQueuingTimer;

        MetricsMeter(String name) {
            queueDepthSummary = DistributionSummary.builder("batch.queue.depth")
                .tags("name", name)
                .register(Metrics.globalRegistry);
            throwAwayCounter = Counter.builder("batch.task.throwaway.count")
                .tags("name", name)
                .register(Metrics.globalRegistry);
            dropCounter = Counter.builder("batch.task.drop.count")
                .tags("name", name)
                .register(Metrics.globalRegistry);
            batchSizeSummary = DistributionSummary.builder("batch.task.summary")
                .tags("name", name)
                .register(Metrics.globalRegistry);
            callLatencyTimer = Timer.builder("batch.task.call.time")
                .tags("name", name)
                .register(Metrics.globalRegistry);
            batchInitTimer = Timer.builder("batch.task.init.time")
                .tags("name", name)
                .register(Metrics.globalRegistry);
            batchExecTimer = Timer.builder("batch.task.exec.time")
                .tags("name", name)
                .register(Metrics.globalRegistry);
            callQueuingTimer = Timer.builder("batch.task.queuing.time")
                .tags("name", name)
                .register(Metrics.globalRegistry);
        }

        void close() {
            Metrics.globalRegistry.remove(queueDepthSummary);
            Metrics.globalRegistry.remove(throwAwayCounter);
            Metrics.globalRegistry.remove(dropCounter);
            Metrics.globalRegistry.remove(batchSizeSummary);
            Metrics.globalRegistry.remove(callLatencyTimer);
            Metrics.globalRegistry.remove(batchInitTimer);
            Metrics.globalRegistry.remove(batchExecTimer);
            Metrics.globalRegistry.remove(callQueuingTimer);
        }
    }
}

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
package com.baidu.bifromq.basescheduler;

import java.util.concurrent.CompletableFuture;

public interface IBatchCall<Req, Resp> {
    /**
     * If no task batched so far
     *
     * @return boolean indicating if the batch is empty
     */
    boolean isEmpty();

    /**
     * If there are enough requests batched return a new builder, otherwise returns empty
     *
     * @return boolean indicating if the batch is full
     */
    boolean isEnough();

    /**
     * Add an async request to this batch
     *
     * @param request add request to the batch for executing
     * @return a future to receive response asynchronously
     */
    CompletableFuture<Resp> add(Req request);

    /**
     * Reset the batch call object to initial state to be reused again
     */
    void reset();

    /**
     * Execute the async batch call
     *
     * @return a future which will complete when batch is done
     */
    CompletableFuture<Void> execute();
}


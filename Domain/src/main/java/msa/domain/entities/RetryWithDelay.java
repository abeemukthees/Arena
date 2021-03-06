/*
 * Copyright 2017, Abhi Muktheeswarar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package msa.domain.entities;

import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

/**
 * Created by Abhimuktheeswarar on 10-06-2017.
 */

public class RetryWithDelay implements Function<Flowable<? extends Throwable>, Publisher<?>> {

    private final int _maxRetries;
    private final int _retryDelayMillis;
    private int _retryCount;

    public RetryWithDelay(final int maxRetries, final int retryDelayMillis) {
        _maxRetries = maxRetries;
        _retryDelayMillis = retryDelayMillis;
        _retryCount = 0;
    }

    // this is a notificationhandler, all that is cared about here
    // is the emission "type" not emission "content"
    // only onNext triggers a re-subscription (onError + onComplete kills it)

    @Override
    public Publisher<?> apply(Flowable<? extends Throwable> inputObservable) {

        // it is critical to use inputObservable in the chain for the result
        // ignoring it and doing your own thing will break the sequence

        return inputObservable.flatMap(
                new Function<Throwable, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(Throwable throwable) {
                        if (++_retryCount < _maxRetries) {

                            // When this Observable calls onNext, the original
                            // Observable will be retried (i.e. re-subscribed)

                            //Log.d("Retrying in %d ms", _retryCount * _retryDelayMillis);

                            return Flowable.timer(_retryCount * _retryDelayMillis, TimeUnit.MILLISECONDS);
                        }

                        //Timber.d("Argh! i give up");

                        // Max retries hit. Pass an error so the chain is forcibly completed
                        // only onNext triggers a re-subscription (onError + onComplete kills it)
                        return Flowable.error(throwable);
                    }
                });
    }
}
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

package msa.arena.movies.list;

import android.util.Log;

import com.github.davidmoten.rx2.util.Pair;

import org.reactivestreams.Publisher;

import java.util.LinkedHashMap;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.processors.ReplayProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import msa.arena.base.BaseViewModel;
import msa.arena.utilities.RxUtilities;
import msa.domain.entities.Movie;
import msa.domain.holder.carrier.ResourceCarrier;
import msa.domain.holder.datastate.DataState;
import msa.domain.holder.datastate.DataStateContainer;
import msa.domain.usecases.GetMovies;

/**
 * Created by Abhimuktheeswarar on 25-08-2017.
 */
public class MovieListViewModel extends BaseViewModel {

    private final GetMovies getMovies;

    private DataStateContainer<LinkedHashMap<String, Movie>> movies;

    private ReplayProcessor<DataStateContainer<LinkedHashMap<String, Movie>>> movies_ReplayProcessor;

    private PublishProcessor<Integer> paginator;

    private int page;

    @Inject
    MovieListViewModel(GetMovies getMovies) {
        this.getMovies = getMovies;
        initializeViewModel();
    }

    @Override
    protected void initializeViewModel() {
        super.initializeViewModel();
        Log.d(TAG, "initializeViewModel");
        paginator = PublishProcessor.create();
        movies = new DataStateContainer<>(new LinkedHashMap<>());
        movies_ReplayProcessor = ReplayProcessor.create();

        DisposableSubscriber<DataStateContainer<LinkedHashMap<String, Movie>>> disposableSubscriber =
                RxUtilities.get(movies_ReplayProcessor);
        compositeDisposable.add(disposableSubscriber);

        page = 1;

        paginator
                .startWith(page)
                .doOnNext(
                        new Consumer<Integer>() {
                            @Override
                            public void accept(Integer integer) throws Exception {
                Log.d(TAG, "doOnNext Paginator = " + page);
                            }
                        })
                .observeOn(Schedulers.io())
                .concatMap(
                        new Function<Integer, Publisher<ResourceCarrier<LinkedHashMap<String, Movie>>>>() {
                            @Override
                            public Publisher<ResourceCarrier<LinkedHashMap<String, Movie>>> apply(
                                    @NonNull Integer page) throws Exception {
                Log.d(TAG, "Paginator = " + page);
                                return getMovies
                                        .execute(GetMovies.Params.newBuilder().page(page).build())
                                        .doOnNext(
                                                new Consumer<ResourceCarrier<LinkedHashMap<String, Movie>>>() {
                                                    @Override
                                                    public void accept(
                                                            ResourceCarrier<LinkedHashMap<String, Movie>>
                                                                    linkedHashMapResourceCarrier)
                                                            throws Exception {
                                                        Log.d(
                                                                TAG,
                                                                "doOnNext linkedHashMapResourceCarrier for page:"
                                                                        + page
                                                                        + " is "
                                                                        + linkedHashMapResourceCarrier.status);
                                                    }
                                                });
                            }
                        })
                .map(
                        linkedHashMapResourceCarrier -> {
                            Log.d(TAG, "status = " + linkedHashMapResourceCarrier.status);
                            Log.d(TAG, "PAGE = " + page);
                            switch (linkedHashMapResourceCarrier.status) {
                case LOADING:
                    if (movies.getDataState() == DataState.REFRESHING
                            || (movies.getDataState() == DataState.ERROR && page == 1)) {
                        movies.getData().clear();
                        movies.setDataState(DataState.REFRESHED);
                    } else movies.setDataState(DataState.LOADING);
                    movies.getData().putAll(linkedHashMapResourceCarrier.data);
                    break;
                case COMPLETED:
                    movies.setDataState(DataState.COMPLETED);
                    break;
                case NETWORK_ERROR:
                    movies.setDataState(DataState.NETWORK_ERROR);
                    movies.setMessage(linkedHashMapResourceCarrier.message);
                    break;
                case ERROR:
                    movies.setDataState(DataState.ERROR);
                    movies.setMessage(linkedHashMapResourceCarrier.message);
                    break;
                            }

                            return movies;
                        })
                .startWith(movies)
                .subscribe(disposableSubscriber);
    }

    Flowable<DataStateContainer<LinkedHashMap<String, Movie>>> getMovies() {
        return movies_ReplayProcessor;
    }

    void loadMore() {

    /*page++;
    paginator.onNext(page);
    Log.d(TAG, "loadMore = " + page);*/

        if (movies.getDataState() != DataState.ERROR) {
            page++;
            Log.d(TAG, "loadMore = " + page);
            movies.setDataState(DataState.LOADING);
            paginator.onNext(page);
        } else Log.d(TAG, "loadMore = " + page + " nope");
    }

    void refresh() {

        if (movies.getDataState() != DataState.REFRESHING) {
            Log.d(TAG, "reset");
            if (movies.getDataState() != DataState.ERROR) movies.setDataState(DataState.REFRESHING);
            page = 1;
            paginator.onNext(page);
        } else {
            movies_ReplayProcessor.onNext(movies);
            Log.d(TAG, "reset nope");
        }
    }

    void reload() {
        paginator.onNext(page);
    }

    void setFavoriteMovie(Pair<String, Boolean> isFavorite) {
        movies.getData().get(isFavorite.left()).setFavorite(isFavorite.right());
        movies_ReplayProcessor.onNext(movies);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "onCleared");
        paginator.onComplete();
        movies_ReplayProcessor.onComplete();
  }
}

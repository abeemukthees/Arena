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

package msa.arena.movies.search;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;
import msa.arena.base.BaseViewModel;
import msa.arena.utilities.RxUtilities;
import msa.domain.entities.Movie;
import msa.domain.holder.carrier.ResourceCarrier;
import msa.domain.holder.datastate.DataState;
import msa.domain.holder.datastate.DataStateContainer;
import msa.domain.usecases.SearchMoviesObservable;

/**
 * Created by Abhimuktheeswarar on 22-08-2017.
 */
public class SearchViewModel extends BaseViewModel {

    private final SearchMoviesObservable searchMoviesObservable;

    DataStateContainer<LinkedHashMap<String, Movie>> dataStateContainer;

    private PublishSubject<String> querySubject;

    private ReplaySubject<DataStateContainer<LinkedHashMap<String, Movie>>>
            dataStateContainerReplaySubject;
    private String queryText;

    @Inject
    SearchViewModel(SearchMoviesObservable searchMoviesObservable) {
        this.searchMoviesObservable = searchMoviesObservable;
        initializeViewModel();
    }

    @Override
    protected void initializeViewModel() {
        super.initializeViewModel();
        dataStateContainer = new DataStateContainer<>();
        querySubject = PublishSubject.create();
        dataStateContainerReplaySubject = ReplaySubject.create();

        DisposableObserver<DataStateContainer<LinkedHashMap<String, Movie>>>
                dataStateContainerDisposableObserver = RxUtilities.get(dataStateContainerReplaySubject);
        compositeDisposable.add(dataStateContainerDisposableObserver);

        querySubject
                .doOnNext(s -> Log.d(TAG, "Search string = " + s))
                .switchMap(
                        new Function<String, Observable<ResourceCarrier<LinkedHashMap<String, Movie>>>>() {
                            @Override
                            public Observable<ResourceCarrier<LinkedHashMap<String, Movie>>> apply(
                                    @NonNull String query) throws Exception {
                Log.d(TAG, "Search text = " + query);
                                return searchMoviesObservable.execute(
                                        SearchMoviesObservable.Params.setQuery(query));
                            }
                        })
                .observeOn(Schedulers.computation())
                .map(
                        linkedHashMapResourceCarrier -> {
                            Log.d(TAG, "Status = " + linkedHashMapResourceCarrier.status);
                            switch (linkedHashMapResourceCarrier.status) {
                case SUCCESS:
                    dataStateContainer.setDataState(DataState.SUCCESS);
                    dataStateContainer.setData(linkedHashMapResourceCarrier.data);
                    break;
                case COMPLETED:
                    dataStateContainer.setDataState(DataState.COMPLETED);
                    dataStateContainer.setData(linkedHashMapResourceCarrier.data);
                    break;
                case ERROR:
                    dataStateContainer.setDataState(DataState.ERROR);
                    dataStateContainer.setData(null);
                    dataStateContainer.setMessage(linkedHashMapResourceCarrier.message);
                    break;
                            }
                            return dataStateContainer;
                        })
                .startWith(dataStateContainer)
                .subscribe(dataStateContainerDisposableObserver);
    }

    public void searchIt(String query) {
        dataStateContainer.setDataState(DataState.LOADING);
        dataStateContainerReplaySubject.onNext(dataStateContainer);
        querySubject.onNext(query);
    }

    public Observable<DataStateContainer<LinkedHashMap<String, Movie>>> getMovieSearchObserver() {
        return dataStateContainerReplaySubject;
    }

    public Observable<DataStateContainer<List<Movie>>> getMovieListSearchObserver() {
        return dataStateContainerReplaySubject.map(
                linkedHashMapDataStateContainer -> {
                    DataStateContainer<List<Movie>> stateContainer = new DataStateContainer<>();
                    stateContainer.setDataState(linkedHashMapDataStateContainer.getDataState());
                    stateContainer.setMessage(linkedHashMapDataStateContainer.getMessage());
                    List<Movie> movies = new ArrayList<>();
                    if ((linkedHashMapDataStateContainer.getDataState() == DataState.SUCCESS
                            || linkedHashMapDataStateContainer.getDataState() == DataState.COMPLETED)
                            && linkedHashMapDataStateContainer.getData() != null) {
                        for (Map.Entry<String, Movie> entry :
                                linkedHashMapDataStateContainer.getData().entrySet()) {
                            Movie movie = entry.getValue();
                            movies.add(movie);
                        }
                    }
                    stateContainer.setData(movies);
                    return stateContainer;
                });
    }

    public Movie getMovieByIndex(int index) {
        List<Movie> movies = new ArrayList<>(dataStateContainer.getData().values());
        return movies.get(index);
    }

    //For testing purposes--------------------------------------------------------------------------

    @Override
    protected void onCleared() {
        super.onCleared();
        querySubject.onComplete();
        dataStateContainerReplaySubject.onComplete();
    }

    public String getQueryText() {
        return queryText;
    }

    public void setQueryText(String queryText) {
        this.queryText = queryText;
  }
}

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

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.jakewharton.rxbinding2.support.v4.widget.RxSwipeRefreshLayout;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.LinkedHashMap;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import msa.arena.R;
import msa.arena.base.BaseActivity;
import msa.arena.base.BaseDisposableSubscriber;
import msa.arena.utilities.EndlessRecyclerViewScrollListener;
import msa.domain.entities.Movie;
import msa.domain.holder.datastate.DataStateContainer;

public class MovieListActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recyclerView_movie)
    RecyclerView recyclerView;

    private MovieListViewModel movieListViewModel;

    private LinearLayoutManager linearLayoutManager;
    private EndlessRecyclerViewScrollListener endlessScrollListener;
    private MovieListController movieListController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        movieListViewModel = getViewModelA(MovieListViewModel.class);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        movieListController = new MovieListController();
        recyclerView.setAdapter(movieListController.getAdapter());

        endlessScrollListener =
                new EndlessRecyclerViewScrollListener(linearLayoutManager, 1) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        //Log.d(TAG, "onLoadMore = " + page);
                        movieListViewModel.loadMore();
                    }
        };

        //swipeRefreshLayout.setEnabled(false);
    }

    @Override
    protected void bind() {

        compositeDisposable.add(RxView.clicks(fab).subscribe(o -> movieListViewModel.reload()));

        compositeDisposable.add(
                getObserveNetworkConnectivity()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aBoolean -> swipeRefreshLayout.setEnabled(aBoolean)));

        recyclerView.addOnScrollListener(endlessScrollListener);

        compositeDisposable.add(
                RxSwipeRefreshLayout.refreshes(swipeRefreshLayout)
                        .subscribe(
                                new Consumer<Object>() {
                                    @Override
                                    public void accept(Object o) throws Exception {
                                        movieListViewModel.refresh();
                                        recyclerView.addOnScrollListener(endlessScrollListener);
                                    }
                                }));

        BaseDisposableSubscriber<DataStateContainer<LinkedHashMap<String, Movie>>> productsSubscriber =
                new BaseDisposableSubscriber<DataStateContainer<LinkedHashMap<String, Movie>>>() {
                    @Override
                    public void onNext(
                            DataStateContainer<LinkedHashMap<String, Movie>> linkedHashMapResourceCarrier) {
                        Log.d(TAG, "data received, state = " + linkedHashMapResourceCarrier.getDataState());

                        switch (linkedHashMapResourceCarrier.getDataState()) {
                            case LOADING:
                                if (!swipeRefreshLayout.isEnabled())
                                    swipeRefreshLayout.setEnabled(true);
                                movieListController.setMovies(linkedHashMapResourceCarrier);
                                break;
                            case REFRESHING:
                                swipeRefreshLayout.setRefreshing(true);
                                break;
                            case REFRESHED:
                                recyclerView.setItemAnimator(null);
                                endlessScrollListener.resetState();
                                swipeRefreshLayout.setRefreshing(false);
                                movieListController.setMovies(linkedHashMapResourceCarrier);
                                new Handler()
                                        .postDelayed(
                                                () -> recyclerView.setItemAnimator(new DefaultItemAnimator()), 200);
                                break;
                            case COMPLETED:
                                recyclerView.removeOnScrollListener(endlessScrollListener);
                                movieListController.setMovies(linkedHashMapResourceCarrier);
                                break;
                            case ERROR:
                                swipeRefreshLayout.setRefreshing(false);
                                movieListController.setMovies(linkedHashMapResourceCarrier);
                                break;
                            case NETWORK_ERROR:
                                swipeRefreshLayout.setRefreshing(false);
                                movieListController.setMovies(linkedHashMapResourceCarrier);
                                break;
                            default:
                                movieListController.setMovies(linkedHashMapResourceCarrier);
                                break;
            }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                    }

                    @Override
                    public void onComplete() {
                    }
        };

        compositeDisposable.add(productsSubscriber);

        movieListViewModel.getMovies().subscribe(productsSubscriber);

        compositeDisposable.add(
                movieListController
                        .getFavoriteChangeObservable()
                        .subscribe(
                                stringBooleanPair -> movieListViewModel.setFavoriteMovie(stringBooleanPair)));

        //Consumer<? super Boolean> refreshing = RxSwipeRefreshLayout.refreshing(swipeRefreshLayout);
        //Observable<Boolean> booleanObservable = Observable.just(true);
        //compositeDisposable.add(booleanObservable.subscribe(refreshing));

    }

    @Override
    protected void unBind() {
    }
}

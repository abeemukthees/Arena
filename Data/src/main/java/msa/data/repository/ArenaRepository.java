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

package msa.data.repository;

import android.support.annotation.VisibleForTesting;
import android.util.Log;

import org.reactivestreams.Publisher;

import java.util.LinkedHashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import msa.data.repository.datasources.local.realm.RealmDataSource;
import msa.data.repository.datasources.remote.RemoteDataSource;
import msa.domain.Repository;
import msa.domain.entities.Lce;
import msa.domain.entities.Movie;
import msa.domain.entities.User;
import msa.domain.holder.carrier.ResourceCarrier;
import msa.domain.holder.carrier.Status;

/**
 * Created by Abhimuktheeswarar on 01-05-2017.
 */

@Singleton
public class ArenaRepository implements Repository {


    private final static String TAG = ArenaRepository.class.getSimpleName();

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    private final DataStoreFactory dataStoreFactory;

    @Inject
    public ArenaRepository(DataStoreFactory dataStoreFactory) {
        this.dataStoreFactory = dataStoreFactory;
    }

    public DataStoreFactory getDataStoreFactory() {
        return dataStoreFactory;
    }

    @Override
    public Observable<User> getUser() {
        return dataStoreFactory.getSharedPreferenceDataSource().getUser();
    }

    @Override
    public Completable updateUser(User user) {
        return dataStoreFactory.getSharedPreferenceDataSource().updateUser(user);
    }

    @Override
    public Observable<Movie> getMovies1(int page) {
        //return dataStoreFactory.getRemoteDataSource().getMovies1(page);
        //return dataStoreFactory.getRemoteDataSource().getMovies1(page);
        return dataStoreFactory.getDummyDataSource().getMovies1(page);

    }

    @Override
    public Observable<List<Movie>> getMovieList(int page) {
        return dataStoreFactory.getDummyDataSource().getMovieList(page);
    }

    @Override
    public Observable<List<Movie>> getMovieList2(int page) {
        return null;
    }

    @Override
    public Observable<LinkedHashMap<String, Movie>> getMovieHashes(int page) {
        return dataStoreFactory.getDummyDataSource().getMovieHashes(page);
    }

    @Override
    public Flowable<Lce<LinkedHashMap<String, Movie>>> getMoviesLce(int page) {
        RemoteDataSource remoteDataSource = dataStoreFactory.getRemoteDataSource();
        return remoteDataSource.getMoviesLce(page);
    }

    @Override
    public Flowable<Lce<LinkedHashMap<String, Movie>>> getMoviesLceR(int page) {
        Log.d(TAG, "getMoviesLceR page = " + page);
        RealmDataSource realmDataSource = dataStoreFactory.getRealmDataStore();
        return realmDataSource.getMoviesLceR(page);
        //return dataStoreFactory.getRealmDataStore().getMoviesLceR(page);
    }

    @Override
    public Flowable<List<Movie>> getMovieFlow(int page) {
        return dataStoreFactory.getDummyDataSource().getMovieFlow(page);
    }

    @Override
    public Flowable<Movie> getMoviesTypeTwo(int page) {
        return dataStoreFactory.getRemoteDataSource().getMoviesTypeTwo(page);
    }

    @Override
    public Flowable<Lce<Movie>> getMoviesTypeTwoLce(int page) {
        return dataStoreFactory.getRemoteDataSource().getMoviesTypeTwoLce(page);
    }

    @Override
    public Observable<List<Movie>> searchMovie(String query) {
        return dataStoreFactory.getRemoteDataSource().searchMovie(query);
    }

    @Override
    public Single<List<Movie>> searchForMovie(String query) {
        return dataStoreFactory.getRemoteDataSource().searchForMovie(query);
    }

    @Override
    public Completable setFavoriteMovie(String movieId, boolean isFavorite) {
        return dataStoreFactory.getRealmDataStore().setFavoriteMovie(movieId, isFavorite);
    }

    @Override
    public Single<ResourceCarrier<LinkedHashMap<String, Movie>>> searchMoviesSingle(String query) {
        return dataStoreFactory.getRemoteDataSource().searchMovies(query);
    }

    @Override
    public Observable<ResourceCarrier<LinkedHashMap<String, Movie>>> searchMoviesObservable(String query) {
        //Log.d(TAG, "calling searchMoviesObservable");
        return dataStoreFactory.getRemoteDataSourceObservable().switchMap(new Function<ResourceCarrier<RemoteDataSource>, Observable<ResourceCarrier<LinkedHashMap<String, Movie>>>>() {
            @Override
            public Observable<ResourceCarrier<LinkedHashMap<String, Movie>>> apply(@NonNull ResourceCarrier<RemoteDataSource> remoteDataSourceResourceCarrier) throws Exception {
                if (remoteDataSourceResourceCarrier.status == Status.SUCCESS && remoteDataSourceResourceCarrier.data != null)
                    return remoteDataSourceResourceCarrier.data.searchMoviesObservable(query);
                else
                    return Observable.just(ResourceCarrier.error(remoteDataSourceResourceCarrier.message));
            }
        });
    }

    @Override
    public Flowable<ResourceCarrier<LinkedHashMap<String, Movie>>> getMovies(int page) {
        //Log.d(TAG, "getMovies -> " + page);
        return dataStoreFactory.getRemoteDataSourceFlowable().switchMap(new Function<ResourceCarrier<RemoteDataSource>, Publisher<? extends ResourceCarrier<LinkedHashMap<String, Movie>>>>() {
            @Override
            public Publisher<? extends ResourceCarrier<LinkedHashMap<String, Movie>>> apply(@NonNull ResourceCarrier<RemoteDataSource> remoteDataSourceResourceCarrier) throws Exception {
                //Log.d(TAG, "status = " + remoteDataSourceResourceCarrier.status);
                if (remoteDataSourceResourceCarrier.status == Status.SUCCESS && remoteDataSourceResourceCarrier.data != null)
                    return remoteDataSourceResourceCarrier.data.getMovies(page);
                else
                    return Flowable.just(ResourceCarrier.error(remoteDataSourceResourceCarrier.message));
            }
        });

        //return dataStoreFactory.getRemoteDataSource().getMovies(page);
    }
}

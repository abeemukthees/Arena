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

package msa.rehearsal.round2.subround2_1;

import android.util.Log;
import android.view.View;

import com.airbnb.epoxy.EpoxyModel;
import com.airbnb.epoxy.OnModelClickListener;
import com.airbnb.epoxy.TypedEpoxyController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.processors.BehaviorProcessor;
import msa.domain.entities.Movie;

/**
 * Created by Abhimuktheeswarar on 01-06-2017.
 */

class MovieTypedEpoxyController extends TypedEpoxyController<List<Movie>> implements OnModelClickListener, MovieItemModel.MovieItemClickListener, MovieItemTypeModel.MovieItemTypeClickListener {

    private final BehaviorProcessor<Movie> movieBehaviorProcessor = BehaviorProcessor.create();
    private final BehaviorProcessor<Integer> integerBehaviorProcessor = BehaviorProcessor.create();


    private LinkedHashMap<String, Movie> linkedHashMap = new LinkedHashMap<>();


    void setMovies(LinkedHashMap<String, Movie> linkedHashMap) {
        this.linkedHashMap = linkedHashMap;
        setData(new ArrayList<>(this.linkedHashMap.values()));
    }

    @Override
    protected void buildModels(List<Movie> data) {


        for (Movie movie : data) {
            {
                //Log.d(MovieEpoxyController.class.getSimpleName(), movie.getMovieName());
                new MovieItemModel_().id(movie.getMovieId()).movieId(movie.getMovieId()).movieName(movie.getMovieName()).isFavorite(movie.isFavorite()).clickCount(1).onClickListener(this).movieItemClickListener(this).addTo(this);
                //new MovieItemTypeModel_().id(movie.getMovieId()).movie(movie).movieItemTypeClickListener(this).addTo(this);
            }
        }
    }


    @Override
    public void onClick(EpoxyModel model, Object parentView, View clickedView, int position) {
        MovieItemModel movieItemModel = (MovieItemModel) model;
        //movieBehaviorProcessor.onNext(getCurrentData().get(position));
        Log.d(MovieTypedEpoxyController.class.getSimpleName(), "id = " + clickedView.getId());
        integerBehaviorProcessor.onNext(position);

    }

    Observable<Movie> getSelectedMovie() {
        return movieBehaviorProcessor.toObservable();
    }

    @Override
    public void onClickFavorite(Movie movie) {
        movieBehaviorProcessor.onNext(movie);

    }

    @Override
    public void onClickFavorite(String movieId, boolean isFavorite) {
        linkedHashMap.get(movieId).setFavorite(isFavorite);
    }
}

package msa.arena.data.repository.datasources.dummy;

import com.msa.domain.entities.Movie;

import org.reactivestreams.Publisher;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.processors.PublishProcessor;
import msa.arena.data.repository.BaseDataSource;

/**
 * Created by Abhimuktheeswarar on 01-05-2017.
 */

public class DummyDataSource implements BaseDataSource {

    Observable<Movie> movieObservable;
    Flowable<Movie> movieFlowable;

    @Override
    public Observable<Movie> getMovies(int page) {
        List<Movie> movieList = new LinkedList<>();
        for (int i = page; i < page + 10; i++) movieList.add(new Movie("id" + i, "Movie " + i));
        if (page > 5) movieList.add(new Movie());
        return Observable.fromIterable(movieList);
    }

    @Override
    public Flowable<Movie> getMoviesTypeTwo(int page) {
        List<Movie> movieList = new LinkedList<>();

        for (int i = page; i < page + 10; i++) movieList.add(new Movie("id" + i, "Movie " + i));
        if (page > 5) movieList.add(new Movie());
        return Flowable.fromIterable(movieList);
        //if (page < 5) return Flowable.fromIterable(movieList);
        //else return Flowable.empty();

    }

    public Flowable<Movie> getMoviesTypeThree(int page) {
        return movieObservable.map(new Function<Movie, Movie>() {
            @Override
            public Movie apply(@NonNull Movie movie) throws Exception {
                return null;
            }
        }).toFlowable(BackpressureStrategy.BUFFER);

    }

    public Flowable<Movie> getMoviesTypeFour(PublishProcessor<Integer> publishProcessor) {
        List<Movie> movieList = new LinkedList<>();
        return publishProcessor.concatMap(new Function<Integer, Publisher<Movie>>() {
            @Override
            public Publisher<Movie> apply(@NonNull Integer page) throws Exception {
                for (int i = page; i < page + 10; i++)
                    movieList.add(new Movie("id" + i, "Movie " + i));
                if (page < 5) return Flowable.fromIterable(movieList);
                else {
                    publishProcessor.onComplete();
                    return Flowable.empty();
                }
            }
        });
    }
}

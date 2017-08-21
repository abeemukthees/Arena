package msa.arc.movies;

import com.msa.domain.entities.User;
import com.msa.domain.usecases.GetMovieHashes;
import com.msa.domain.usecases.UpdateUserTypeOne;

import javax.inject.Inject;

import io.reactivex.Completable;
import msa.arc.base.BaseViewModel;

/**
 * Created by Abhimuktheeswarar on 08-06-2017.
 */
public class MoviesViewModel extends BaseViewModel {

    private final GetMovieHashes getMovieHashes;
    private final UpdateUserTypeOne updateUserTypeOne;
    public int a = 2;


    @Inject
    public MoviesViewModel(GetMovieHashes getMovieHashes, UpdateUserTypeOne updateUserTypeOne) {
        this.getMovieHashes = getMovieHashes;
        this.updateUserTypeOne = updateUserTypeOne;
    }

    public int getA() {
        return a;
    }

    public void add() {
        a++;
    }

    Completable updateUser(String displayName) {
        return updateUserTypeOne.execute(UpdateUserTypeOne.Params.setUser(new User("id_" + displayName, displayName)));
    }


}
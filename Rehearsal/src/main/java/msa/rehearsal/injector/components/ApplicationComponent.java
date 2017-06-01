package msa.rehearsal.injector.components;

import android.content.Context;

import com.msa.domain.Repository;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import io.reactivex.Scheduler;
import msa.rehearsal.injector.modules.ApplicationModule;

/**
 * Created by Abhimuktheeswarar on 01-05-2017.
 */

@Singleton // Constraints this component to one-per-application or unscoped bindings.
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    //Exposed to sub-graphs.
    Context context();

    @Named("ui_thread")
    Scheduler uiThread();

    @Named("executor_thread")
    Scheduler executorThread();

    Repository repository();
}
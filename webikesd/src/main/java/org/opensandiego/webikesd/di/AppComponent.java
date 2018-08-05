package org.opensandiego.webikesd.di;

import android.app.Application;

import org.opensandiego.webikesd.WeBikeSdApplication;
import org.opensandiego.webikesd.data.roomdb.AppDatabaseModule;
import org.opensandiego.webikesd.data.source.cyclepoint.CyclePointRepositoryModule;
import org.opensandiego.webikesd.data.source.progress.ProgressDataSourceModule;
import org.opensandiego.webikesd.data.source.trip.TripRepositoryModule;
import org.opensandiego.webikesd.data.source.tripcyclepoint.TripCyclePointRepositoryModule;
import org.opensandiego.webikesd.di.modules.ActivityBindingModule;
import org.opensandiego.webikesd.di.modules.ApplicationModule;
import org.opensandiego.webikesd.di.modules.SchedulerProviderModule;
import org.opensandiego.webikesd.di.modules.SharedPreferenceModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * This is a Dagger component. Refer to {@link WeBikeSdApplication} for the list of Dagger
 * components
 * used in this application.
 * <p>
 * Even though Dagger allows annotating a {@link Component} as a singleton, the code
 * itself must ensure only one instance of the class is created. This is done in
 * {@link WeBikeSdApplication}.
 * //{@link AndroidSupportInjectionModule}
 * // is the module from Dagger.Android that helps with the generation
 * // and location of subcomponents.
 */
@Singleton
@Component(modules = {
    ProgressDataSourceModule.class,
    CyclePointRepositoryModule.class,
    TripRepositoryModule.class,
    TripCyclePointRepositoryModule.class,
    AppDatabaseModule.class,

    // app wide modules
    ApplicationModule.class,
    ActivityBindingModule.class,
    SchedulerProviderModule.class,
    SharedPreferenceModule.class,
    AndroidSupportInjectionModule.class})
public interface AppComponent extends AndroidInjector<WeBikeSdApplication> {

  // TasksRepository getTasksRepository();

  // Gives us syntactic sugar. we can then do DaggerAppComponent.builder().application(this)
  // .build().inject(this);
  // never having to instantiate any modules or say which module we are passing the application to.
  // Application will just be provided into our app graph now.
  @Component.Builder
  interface Builder {

    @BindsInstance
    AppComponent.Builder application(Application application);

    AppComponent build();
  }
}

package org.opensandiego.webikesd;

import android.app.Application;

import org.opensandiego.webikesd.di.DaggerAppComponent;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;
import timber.log.Timber;

/**
 * We create a custom {@link Application} class that extends  {@link DaggerApplication}.
 * We then override applicationInjector() which tells Dagger how to make our @Singleton Component
 * We never have to call `component.inject(this)` as {@link DaggerApplication} will do that for us.
 */
public class WeBikeSdApplication extends DaggerApplication {
//    @Inject
//    TasksRepository tasksRepository;

  @Override
  protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
    return DaggerAppComponent.builder().application(this).build();
  }

  @Override
  public void onCreate() {
    super.onCreate();
    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
    }
  }

  //    /**
//     * Our Espresso tests need to be able to get an instance of the {@link TasksRepository}
//     * so that we can delete all tasks before running each test
//     */
//    @VisibleForTesting
//    public TasksRepository getTasksRepository() {
//        return tasksRepository;
//    }
}

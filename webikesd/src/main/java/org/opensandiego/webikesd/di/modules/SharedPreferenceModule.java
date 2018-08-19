package org.opensandiego.webikesd.di.modules;

/*
  Created by lamch on 12/30/2017.
 */

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
abstract public class SharedPreferenceModule {

  @NonNull
  @Singleton
  @Provides
  static SharedPreferences provideSharedPreferences(Application context) {
    return context.getApplicationContext().getSharedPreferences("app_shared_preference",
        Context.MODE_PRIVATE);
  }
}

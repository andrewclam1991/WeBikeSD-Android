package org.opensandiego.webikesd.data.source.progress;

import android.content.SharedPreferences;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import timber.log.Timber;

/**
 * Example progress model implementation,
 * Note: uses and depends on Android framework shared preferences
 */
class ProgressModel implements ProgressDataSource {

  @NonNull
  private final SharedPreferences mSharedPreferences;

  @NonNull
  @ProgressKey
  private final String mKey;

  @Inject
  ProgressModel(@NonNull SharedPreferences sharedPreferences,
                @NonNull @ProgressKey String key) {
    mSharedPreferences = sharedPreferences;
    mKey = key;
  }

  @Override
  public Flowable<Integer> getCurrentProgress() {
    return Flowable.create(emitter -> {
      // Register and listen for future progress updates
      SharedPreferences.OnSharedPreferenceChangeListener listener = (sharedPreferences, key) -> {
        if (key.equals(mKey)) {
          int updateProgress = sharedPreferences.getInt(key, 0);
          emitter.onNext(updateProgress);
          Timber.d("Progress value changed to: %s", updateProgress);
        }
      };
      mSharedPreferences.registerOnSharedPreferenceChangeListener(listener);

      // Initial emission, get the last known progress value
      int lastKnownProgress = mSharedPreferences.getInt(mKey, 0);
      emitter.onNext(lastKnownProgress);

    }, BackpressureStrategy.BUFFER);
  }

  @Override
  public Completable setCurrentProgress(int progress) {
    return Completable.create(emitter -> {
      if (mSharedPreferences.edit().putInt(mKey, progress).commit()) {
        Timber.d("Progress value set to: %s", progress);
        emitter.onComplete();
      } else {
        emitter.onError(new RuntimeException("Unable to update current progress"));
      }
    });
  }

  @Override
  public Completable incrementCurrentProgress() {
    return Completable.create(emitter -> {
      int lastKnownProgress = mSharedPreferences.getInt(mKey, 0);
      if (mSharedPreferences.edit().putInt(mKey, lastKnownProgress + 1).commit()) {
        Timber.d("Progress value incremented from: %s", lastKnownProgress);
        emitter.onComplete();
      } else {
        emitter.onError(new RuntimeException("Unable to increment current progress"));
      }
    });
  }
}

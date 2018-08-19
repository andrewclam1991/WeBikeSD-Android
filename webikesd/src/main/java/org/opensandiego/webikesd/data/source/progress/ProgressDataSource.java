package org.opensandiego.webikesd.data.source.progress;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;

public interface ProgressDataSource {
  /**
   * @return Observable stream of progress updates
   */
  @NonNull
  Flowable<Integer> getCurrentProgress();

  /**
   * Allow client to set progress update
   *
   * @param progress current progress update
   * @return Observable event that indicates whether the set operation was successful
   */
  @NonNull
  Completable setCurrentProgress(int progress);

  /**
   * Allow client to increment progress counter
   *
   * @return Observable event that indicates whether the set operation was successful
   */
  @NonNull
  Completable incrementCurrentProgress();
}

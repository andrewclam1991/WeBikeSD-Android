package org.opensandiego.webikesd.views.dashboard.progressupdate;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import org.opensandiego.webikesd.data.source.progress.ProgressDataSource;
import org.opensandiego.webikesd.util.schedulers.BaseSchedulerProvider;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

/**
 * Example progress presenter
 */
class ProgressPresenter implements ProgressContract.Presenter {

  // View
  @Nullable
  private ProgressContract.View mView;

  // Data
  @NonNull
  private final BaseSchedulerProvider mSchedulerProvider;
  @NonNull
  private final CompositeDisposable mCompositeDisposable;
  @NonNull
  private final ProgressDataSource mProgressDataSource;

  @Inject
  ProgressPresenter(@NonNull BaseSchedulerProvider schedulerProvider,
                    @NonNull ProgressDataSource dataSource) {
    mSchedulerProvider = schedulerProvider;
    mCompositeDisposable = new CompositeDisposable();
    mProgressDataSource = dataSource;
  }

  @Override
  public void setView(ProgressContract.View view) {
    mView = view;
    getProgress();
  }

  @Override
  public void dropView() {
    mView = null;
    mCompositeDisposable.clear();
  }

  @Override
  public void getProgress() {
    Disposable disposable = mProgressDataSource.getCurrentProgress()
        .subscribeOn(mSchedulerProvider.io())
        .observeOn(mSchedulerProvider.ui())
        .forEach(this::handleEachProgressUpdate);

    mCompositeDisposable.add(disposable);
  }

  @Override
  public void setProgress(int progress) {
    Disposable disposable = mProgressDataSource.setCurrentProgress(progress)
        .subscribeOn(mSchedulerProvider.io())
        .observeOn(mSchedulerProvider.ui())
        .subscribe(this::handleOnSetProgressComplete, this::handleOnSetProgressFailure);

    mCompositeDisposable.add(disposable);
  }

  @Override
  public void incrementProgress() {
    Disposable disposable = mProgressDataSource.incrementCurrentProgress()
        .subscribeOn(mSchedulerProvider.io())
        .observeOn(mSchedulerProvider.ui())
        .subscribe(this::handleOnSetProgressComplete, this::handleOnSetProgressFailure);

    mCompositeDisposable.add(disposable);
  }

  @UiThread
  private void handleEachProgressUpdate(@NonNull Integer currentProgress) {
    Timber.d("current progress changed: %s", currentProgress);
    if (mView == null || !mView.isActive()) { return; }
    mView.showCurrentProgress(currentProgress);
  }

  @UiThread
  private void handleOnSetProgressComplete() {
    Timber.d("Set progress complete.");
    if (mView == null || !mView.isActive()) { return; }
    mView.showSetCurrentProgressSuccess();
  }

  private void handleOnSetProgressFailure(@NonNull Throwable throwable) {
    Timber.e("Unable to set progress.");
    Timber.w(throwable);
    if (mView == null || !mView.isActive()) { return; }
    mView.showSetCurrentProgressFailure();
  }
}

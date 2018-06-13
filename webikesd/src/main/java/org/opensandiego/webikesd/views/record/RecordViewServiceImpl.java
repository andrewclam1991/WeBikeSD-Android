package org.opensandiego.webikesd.views.record;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.opensandiego.webikesd.data.model.CyclePoint;
import org.opensandiego.webikesd.data.model.TripData;

import java.util.UUID;

class RecordViewServiceImpl extends Service implements RecordContract.ViewService {
  // Binder given to clients
  private final IBinder mBinder = new ServiceBinder();

  // Visible views
  private RecordContract.View mView;

  // Internal states
  private RecordContract.RecordState mCurrentState;
  private RecordContract.RecordState mNoTripState;
  private RecordContract.RecordState mTripStartedState;
  private RecordContract.RecordState mTripPausedState;

  // TODO inject presenter
  private RecordContract.Presenter mPresenter;

  // Location Provider
  private FusedLocationProviderClient mFusedLocationClient;

  @Override
  public void setView(RecordContract.View view) { mView = view; }

  @Override
  public void dropView() { mView = null; }

  @Override
  public void onCreate() {
    super.onCreate();
    mNoTripState = new NoTripRecordState();
    mTripStartedState = new TripStartedState();
    mTripPausedState = new TripPausedState();
    mCurrentState = mNoTripState;
    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
  }

  @Override
  public void showTrip(@NonNull TripData tripData) {
    // TODO implt show trip ui
    mView.showTripDistance(tripData.getDistance());
  }

  @Override
  public boolean isActive() { return true; }

  @Override
  public void start() { mCurrentState.start(); }

  @Override
  public void update() { mCurrentState.update(); }

  @Override
  public void pause() { mCurrentState.pause(); }

  @Override
  public void cancel() { mCurrentState.cancel(); }

  @Override
  public void complete() { mCurrentState.complete(); }

  @Override
  public void setState(RecordContract.RecordState state) { mCurrentState = state; }

  @Override
  public RecordContract.RecordState getState() { return mCurrentState; }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) { return mBinder; }

  /**
   * Class used for the client Binder.  Because we know this service always
   * runs in the same process as its clients, we don't need to deal with IPC.
   */
  public class ServiceBinder extends Binder {
    // returns an instance of this class for clients to call public methods
    RecordContract.ViewService getService() { return RecordViewServiceImpl.this; }
  }

  /**
   * Concrete internal {@link RecordContract.RecordState} when there is no
   * trip or trip has stopped.
   */
  private class NoTripRecordState implements RecordContract.RecordState {
    @Override
    public void start() {
      // create new trip
      mPresenter.saveTrip(new TripData(UUID.randomUUID().toString()));
      // todo start location update
    }

    @Override
    public void update() { start(); }

    @Override
    public void pause() { /* invalid request, ignore */}

    @Override
    public void cancel() { stopSelf(); }

    @Override
    public void complete() { stopSelf(); }
  }

  /**
   * Concrete internal {@link RecordContract.RecordState} when trip
   * is active and started
   */
  private class TripStartedState implements RecordContract.RecordState{

    @Override
    public void start() { /* Already started, ignore*/}

    @Override
    public void update() {
      CyclePoint pt = new CyclePoint(
          UUID.randomUUID().toString(),
          0,
          0,
          System.currentTimeMillis());
      mPresenter.updateTrip(pt);
    }

    @Override
    public void pause() {
      mCurrentState = mTripPausedState;
    }

    @Override
    public void cancel() {
      mCurrentState = mNoTripState;
      mCurrentState.cancel();
    }

    @Override
    public void complete() {
      mCurrentState = mNoTripState;
      mCurrentState.complete();
    }
  }

  /**
   * Concrete internal {@link RecordContract.RecordState} when trip
   * is inactive and paused, but not completed.
   */
  private class TripPausedState implements RecordContract.RecordState{

    @Override
    public void start() {
      // start
      mCurrentState = mTripStartedState;
    }

    @Override
    public void update() { start(); }

    @Override
    public void pause() { /* Already paused, ignore */ }

    @Override
    public void cancel() {
      // TODO delete trip
      mCurrentState = mNoTripState;
      mCurrentState.cancel();
    }

    @Override
    public void complete() {
      // TODO save trip with complete status
      mCurrentState = mNoTripState;
      mCurrentState.complete();
    }
  }
}

package org.opensandiego.webikesd.views.dashboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import org.opensandiego.webikesd.R;
import org.opensandiego.webikesd.util.ActivityUtils;
import org.opensandiego.webikesd.util.idlingresource.EspressoIdlingResource;
import org.opensandiego.webikesd.views.dashboard.monitor.MonitorFragment;
import org.opensandiego.webikesd.views.dashboard.tracking.TrackingFragment;

import javax.inject.Inject;

import dagger.Lazy;
import dagger.android.support.DaggerAppCompatActivity;

/**
 * Dash board activity
 * Responsibilities:
 * - show user current trip metrics
 * - let user control the state of the current trip
 */
public class DashboardActivity extends DaggerAppCompatActivity {

  @Inject
  Lazy<MonitorFragment> mMonitorFragmentProvider;

  @Inject
  Lazy<TrackingFragment> mTrackingFragmentProvider;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dashboard);
    Toolbar toolbar = findViewById(R.id.toolbar);

    setSupportActionBar(toolbar);

    ActionBar actionBar = getSupportActionBar();
    assert actionBar != null;
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setDisplayShowHomeEnabled(true);

    setupMonitorFragment();
    setupTrackingFragment();
  }

  private void setupMonitorFragment() {
    MonitorFragment fragment = (MonitorFragment) getSupportFragmentManager()
        .findFragmentById(R.id.fragment_container_top);

    if (fragment == null) {
      // Create the fragment
      fragment = mMonitorFragmentProvider.get();
      ActivityUtils.addFragmentToActivity(
          getSupportFragmentManager(), fragment, R.id.fragment_container_top);
    }
  }

  private void setupTrackingFragment() {
    TrackingFragment fragment = (TrackingFragment) getSupportFragmentManager()
        .findFragmentById(R.id.fragment_container_bottom);

    if (fragment == null) {
      // Create the fragment
      fragment = mTrackingFragmentProvider.get();
      ActivityUtils.addFragmentToActivity(
          getSupportFragmentManager(), fragment, R.id.fragment_container_bottom);
    }
  }

  @Override
  public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }

  @NonNull
  @VisibleForTesting
  public IdlingResource getCountingIdlingResource() {
    return EspressoIdlingResource.getIdlingResource();
  }
}

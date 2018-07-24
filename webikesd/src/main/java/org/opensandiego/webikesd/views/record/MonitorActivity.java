package org.opensandiego.webikesd.views.record;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import org.opensandiego.webikesd.R;
import org.opensandiego.webikesd.util.ActivityUtils;
import org.opensandiego.webikesd.util.idlingresource.EspressoIdlingResource;

import javax.inject.Inject;

import dagger.Lazy;
import dagger.android.support.DaggerAppCompatActivity;

/**
 * Lean framework activity
 * Responsibilities:
 * 1. load and keep framework fragment alive as long as activity is active
 */
public class MonitorActivity extends DaggerAppCompatActivity {

  @Inject
  Lazy<MonitorFragment> mFragmentProvider;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_record);
    Toolbar toolbar = findViewById(R.id.toolbar);

    setSupportActionBar(toolbar);

    ActionBar actionBar = getSupportActionBar();
    assert actionBar != null;
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setDisplayShowHomeEnabled(true);

    MonitorFragment fragment = (MonitorFragment) getSupportFragmentManager()
        .findFragmentById(R.id.fragment_container);

    if (fragment == null) {
      // Create the fragment
      fragment = mFragmentProvider.get();
      ActivityUtils.addFragmentToActivity(
          getSupportFragmentManager(), fragment, R.id.fragment_container);
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

package org.opensandiego.webikesd.views.dashboard.progressupdate;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.opensandiego.webikesd.R;
import org.opensandiego.webikesd.di.ActivityScoped;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;

/**
 * A simple {@link DaggerFragment} subclass.
 */
@ActivityScoped
public class ProgressFragment extends DaggerFragment implements ProgressContract.View {

  @BindView(R.id.determinate_pb)
  ProgressBar mDeterminatePb;

  @BindView(R.id.test_update_progress_btn)
  View mTestUpdateProgressBtn;

  @Inject
  ProgressContract.Presenter mPresenter;

  @Inject
  public ProgressFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View rootView = inflater.inflate(R.layout.fragment_progress, container, false);
    ButterKnife.bind(this, rootView);
    mTestUpdateProgressBtn.setOnClickListener(v -> mPresenter.incrementProgress());
    return rootView;
  }

  @Override
  public void onStart() {
    super.onStart();
    mPresenter.setView(this);
  }

  @Override
  public void onStop() {
    super.onStop();
    mPresenter.dropView();
  }


  @Override
  public void showCurrentProgress(int percentage) {
    mDeterminatePb.setProgress(percentage);
  }

  @Override
  public void showGetCurrentProgressError() {
    Toast.makeText(getActivity(), "Unable to get current progress.", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void showSetCurrentProgressSuccess() {
    Toast.makeText(getActivity(), "Set current progress success.", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void showSetCurrentProgressFailure() {
    Toast.makeText(getActivity(), "Unable to set current progress.", Toast.LENGTH_SHORT).show();
  }

  @Override
  public boolean isActive() {
    return isAdded();
  }
}

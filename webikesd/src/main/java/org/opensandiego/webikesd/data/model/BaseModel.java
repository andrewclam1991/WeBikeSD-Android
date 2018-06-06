package org.opensandiego.webikesd.data.model;

import android.support.annotation.NonNull;

/**
 * Base class for each BaseModel class
 */
public interface BaseModel {

  @NonNull
  String getUid();

  void setUid(@NonNull String id);
}

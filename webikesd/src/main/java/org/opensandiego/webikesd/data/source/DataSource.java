package org.opensandiego.webikesd.data.source;

import android.support.annotation.NonNull;

import com.google.common.base.Optional;

import org.opensandiego.webikesd.data.model.BaseEntity;


import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * API to the model layer
 */
public interface DataSource<E extends BaseEntity> {

  /**
   * Get item by id of type {@link E} from its model layer
   * @param id unique id of a {@link E} type
   * @return observable that when subscribed to, would fetch and emit the item
   * and error otherwise.
   */
  Flowable<Optional<E>> get(@NonNull String id);

  /**
   * Gets all items of type {@link E} from the model layer
   * @return a hot observable that when subscribed to, emits model updates to the
   * list of {@link E} items.
   */
  Flowable<List<E>> getAll();

  /**
   * Puts a single item of type {@link E} to the model layer
   * @param item item to be put into the model layer
   * @return an observable that when subscribed to, emits {@link Completable#complete()}
   * when the operation is complete and error otherwise.
   */
  Completable put(@NonNull E item);

  /**
   * Puts a list of items of type {@link E} to the model layer
   * @param items collection of items to be put into the model layer
   * @return an observable that when subscribed to, emits {@link Completable#complete()}
   * when the operation is complete and error otherwise.
   */
  Completable put(@NonNull List<E> items);

  /**
   * Updates a particular item of type {@link E} to the model layer
   * @param item the updated item data
   * @return an observable that when subscribed to, emits {@link Completable#complete()}
   * when the operation is complete and error otherwise.
   */
  Completable update(@NonNull E item);

  /**
   * Deletes a particular item by id of type {@link E} from the model layer
   * @param id unique id of the item
   * @return an observable that when subscribed to, emits {@link Completable#complete()}
   * when the operation is complete and error otherwise.
   */
  Completable delete(@NonNull String id);

  /**
   * Deletes all items of type {@link E} from the model layer
   * @return an observable that when subscribed to, emits {@link Completable#complete()}
   * when the operation is complete and error otherwise.
   */
  Completable deleteAll();
}

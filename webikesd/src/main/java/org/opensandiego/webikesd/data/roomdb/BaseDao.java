package org.opensandiego.webikesd.data.roomdb;

import com.google.common.base.Optional;

import org.opensandiego.webikesd.data.model.BaseModel;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Base CRUD operations that all {@link BaseModel}
 * should have.
 */
public interface BaseDao<E extends BaseModel> {
  /**
   * Inserts an item into the model layer
   *
   * @param item
   */
  void insert(E item);

  /**
   * Inserts a list of items into the model layer
   *
   * @param items
   */
  void insertAll(List<E> items);

  /**
   * Updates a particular item within the model layer
   *
   * @param item
   */
  void update(E item);

  /**
   * Gets an emission of items from the model layer
   *
   * @return
   */
  Flowable<List<E>> getAll();

  /**
   * Gets an emission of a particular item from the model layer
   *
   * @param itemId
   * @return
   */
  Flowable<Optional<E>> get(String itemId);

  /**
   * Deletes a particular item within the model layer
   *
   * @param itemId
   */
  void delete(String itemId);

  /**
   * Delete all items of type {@link E} within the model layer
   */
  void deleteAll();
}

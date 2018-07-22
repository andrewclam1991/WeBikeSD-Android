package org.opensandiego.webikesd.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.google.common.base.Optional;

import org.opensandiego.webikesd.data.model.BaseModel;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Flowable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Generic Repository implementation of a {@link DataSource <>}
 * that provides implementation for common base CRUD methods
 * @param <E>
 */
public class Repository<E extends BaseModel> implements DataSource<E> {

  @NonNull
  private final DataSource<E> mLocalDataSource;

  @NonNull
  private final DataSource<E> mRemoteDataSource;

  /**
   * This variable has package local visibility so it can be accessed from tests.
   */
  @VisibleForTesting
  @NonNull
  final Map<String, E> mCachedItems;

  /**
   * Marks the cache as invalid, to force an onTripUpdate the next time data is requested. This variable
   * has package local visibility so it can be accessed from tests.
   * Note: default set flag to false, so at init (with mCachedItems empty),
   * repository will always try local-first
   */
  @VisibleForTesting
  boolean mCacheIsDirty = false;

  @VisibleForTesting
  protected Repository(@NonNull @Local DataSource<E> localDataSource,
                       @NonNull @Remote DataSource<E> remoteDataSource){
    mLocalDataSource = checkNotNull(localDataSource, "localDataSource can't be null!");
    mRemoteDataSource = checkNotNull(remoteDataSource, "remoteDataSource can't be null!");
    mCachedItems = new LinkedHashMap<>();
  }

  @Override
  public void refresh() {
    mCacheIsDirty = true;
  }

  @NonNull
  @Override
  public Completable put(@NonNull E item) {
    mCachedItems.put(item.getUid(), checkNotNull(item));
    return mLocalDataSource.put(item).andThen(mRemoteDataSource.put(item));
  }

  @NonNull
  @Override
  public Completable put(@NonNull List<E> items) {
    for (E item : items) {
      mCachedItems.put(item.getUid(), checkNotNull(item));
    }
    return mLocalDataSource.put(items).andThen(mRemoteDataSource.put(items));
  }

  @NonNull
  @Override
  public Flowable<List<E>> getAll() {
    // Respond immediately with cache if available and not dirty
    if (!mCachedItems.isEmpty() && !mCacheIsDirty) {
      return Flowable.fromIterable(mCachedItems.values()).toList().toFlowable();
    }

    // Repository starts with clean cache (mCacheIsDirty = false);
    // Queries local first
    // if local empty, try remote,
    // if local has data, add each item to cache, return data.
    // if remote empty, return no data.
    // if remote has data, add each item to local and cache, mark cache as clean return data.

    Flowable<List<E>> remoteItems = getAndSaveRemoteItems();

    if (mCacheIsDirty) {
      // refresh local data with remote
      return remoteItems;
    } else {
      // query local and remote data sources, emit the first result
      Flowable<List<E>> localItems = getAndCacheLocalItems();
      return Flowable.concat(localItems, remoteItems)
          .filter(items -> !items.isEmpty())
          .firstOrError()
          .toFlowable();
    }
  }

  @NonNull
  @Override
  public Flowable<Optional<E>> get(@NonNull String itemId) {
    checkNotNull(itemId);

    final E cachedItem = getItemWithIdFromCache(itemId);

    // Respond with the if it is available in cache
    if (cachedItem != null) {
      return Flowable.just(Optional.of(cachedItem));
    }

    // Create an Observable to query the item in the local data source
    Flowable<Optional<E>> localItem = getLocalItemById(itemId);

    // Create an Observable to query the item in the remote data source, and download it
    Flowable<Optional<E>> remoteItem = getRemoteItemById(itemId);

    // Concat the local and remote sources into one,
    return Flowable.concat(localItem, remoteItem)
        .firstElement()
        .toFlowable();
  }

  @NonNull
  @Override
  public Completable update(@NonNull E item) {
    mCachedItems.put(item.getUid(), item);
    return mLocalDataSource.update(item).andThen(mRemoteDataSource.update(item));
  }

  @NonNull
  @Override
  public Completable delete(@NonNull String entityId) {
    if (!mCachedItems.isEmpty() && mCachedItems.containsKey(entityId)) {
      mCachedItems.remove(entityId);
    }
    return mLocalDataSource.delete(entityId).andThen(mRemoteDataSource.delete(entityId));
  }

  @NonNull
  @Override
  public Completable deleteAll() {
    mCachedItems.clear();
    return mLocalDataSource.deleteAll().andThen(mRemoteDataSource.deleteAll());
  }

  @NonNull
  private Flowable<List<E>> getAndCacheLocalItems() {
    return mLocalDataSource.getAll()
        .flatMap(items -> Flowable.fromIterable(items)
            .doOnNext(this::saveItemToCache)
            .toList()
            .toFlowable()
        )
        .takeWhile(items -> !items.isEmpty());// this completes the stream when the list becomes empty
  }

  @NonNull
  private Flowable<List<E>> getAndSaveRemoteItems() {
    return mRemoteDataSource.getAll()
        .flatMap(items -> Flowable.fromIterable(items)
            .doOnNext(item -> mLocalDataSource.put(item).andThen(saveItemToCache(item)))
            .toList()
            .toFlowable()
        ).doOnComplete(() -> mCacheIsDirty = false);
  }

  @NonNull
  private Flowable<Optional<E>> getLocalItemById(@NonNull final String itemId) {
    return mLocalDataSource.get(itemId)
        .takeWhile(Optional::isPresent)
        .flatMap(itemOptional -> {
          if (itemOptional.isPresent()) {
            E item = itemOptional.get();
            return saveItemToCache(item)
                .andThen(Flowable.just(itemOptional));
          }else{
            return Flowable.just(Optional.absent());
          }
        });
  }

  @NonNull
  private Flowable<Optional<E>> getRemoteItemById(@NonNull final String itemId) {
    return mRemoteDataSource.get(itemId)
        .flatMap(itemOptional -> {
          if (itemOptional.isPresent()) {
            E item = itemOptional.get();
            return mLocalDataSource.put(item)
                .andThen(saveItemToCache(item))
                .andThen(Flowable.just(itemOptional));
          }else{
            return Flowable.just(Optional.absent());
          }
        });
  }

  @VisibleForTesting
  private Completable saveItemToCache(@NonNull E item) {
    mCachedItems.put(item.getUid(), item);
    return Completable.complete();
  }

  @Nullable
  private E getItemWithIdFromCache(@NonNull final String itemId) {
    checkNotNull(itemId);

    if (mCachedItems.isEmpty()) {
      return null;
    } else {
      // still might not be able to find the item with id, return a nullable item
      return mCachedItems.get(itemId);
    }
  }
}

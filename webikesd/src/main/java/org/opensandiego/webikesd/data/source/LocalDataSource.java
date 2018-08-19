package org.opensandiego.webikesd.data.source;

import android.support.annotation.NonNull;

import com.google.common.base.Optional;

import org.opensandiego.webikesd.data.model.BaseModel;
import org.opensandiego.webikesd.data.roomdb.BaseDao;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * Abstraction of Local CRUD operations
 *
 * @param <E> type of {@link BaseModel}
 */
public abstract class LocalDataSource<E extends BaseModel> implements DataSource<E> {

  @NonNull
  private final BaseDao<E> mDao;

  protected LocalDataSource(@NonNull BaseDao<E> dao) {
    mDao = dao;
  }

  @Override
  public void refresh() { /* no implementation */ }

  @NonNull
  @Override
  public Completable add(@NonNull E item) {
    return Completable.create(emitter -> {
      mDao.insert(item);
      emitter.onComplete();
    });
  }

  @NonNull
  @Override
  public Completable add(@NonNull List<E> items) {
    return Completable.create(emitter -> {
      mDao.insertAll(items);
      emitter.onComplete();
    });
  }

  @NonNull
  @Override
  public Flowable<List<E>> getAll() { return mDao.getAll(); }

  @NonNull
  @Override
  public Flowable<Optional<E>> get(@NonNull String entityId) { return mDao.get(entityId); }

  @NonNull
  @Override
  public Completable update(@NonNull E item) {
    return Completable.create(emitter -> {
      mDao.update(item);
      emitter.onComplete();
    });
  }

  @NonNull
  @Override
  public Completable delete(@NonNull String entityId) {
    return Completable.create(emitter -> {
      mDao.delete(entityId);
      emitter.onComplete();
    });
  }

  @NonNull
  @Override
  public Completable deleteAll() {
    return Completable.create(emitter -> {
      mDao.deleteAll();
      emitter.onComplete();
    });
  }
}

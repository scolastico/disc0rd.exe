package com.scolastico.discord_exe.etc;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LockableObjectHolder<T> {

  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
  private T object;

  public LockableObjectHolder(T object) {
    this.object = object;
  }

  public T getObject() {
    return getObject(false);
  }

  public T getObject(boolean lock) {
    this.lock.writeLock().lock();
    if (!lock) this.lock.writeLock().unlock();
    return object;
  }

  public void setObject(T object) {
    this.object = object;
    if (lock.isWriteLocked()) if (lock.writeLock().isHeldByCurrentThread()) {
      lock.writeLock().unlock();
    } else {
      ErrorHandler.getInstance().handle(new Exception("Cant unlock from other Thread!"));
    }
  }

  public boolean isLocked() {
    return lock.isWriteLocked();
  }

}

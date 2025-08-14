package co.hyperflex.services;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.stereotype.Component;

@Component
public class ClusterLockService {
  private final Lock lock = new ReentrantLock();

  public Lock getLock() {
    return lock;
  }

  public void lock() {
    lock.lock();
  }

  public void unlock() {
    lock.unlock();
  }
}


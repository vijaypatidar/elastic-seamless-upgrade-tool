package co.hyperflex.clients.elastic.dto.nodes;

public class IngestTotal {
  private long count;
  private long current;
  private long failed;
  private long timeInMillis;

  public void setCount(long count) {
    this.count = count;
  }

  public void setCurrent(long current) {
    this.current = current;
  }

  public void setFailed(long failed) {
    this.failed = failed;
  }

  public void setTimeInMillis(long timeInMillis) {
    this.timeInMillis = timeInMillis;
  }

  public long getCount() {
    return count;
  }

  public long getCurrent() {
    return current;
  }

  public long getFailed() {
    return failed;
  }

  public long getTimeInMillis() {
    return timeInMillis;
  }
}

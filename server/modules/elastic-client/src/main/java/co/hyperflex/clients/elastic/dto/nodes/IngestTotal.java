package co.hyperflex.clients.elastic.dto.nodes;

public class IngestTotal {
  private final long count;
  private final long current;
  private final long failed;
  private final long timeInMillis;

  public IngestTotal(long count, long current, long failed, long timeInMillis) {
    this.count = count;
    this.current = current;
    this.failed = failed;
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

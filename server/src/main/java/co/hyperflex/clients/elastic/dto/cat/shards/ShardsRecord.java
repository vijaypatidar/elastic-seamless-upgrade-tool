package co.hyperflex.clients.elastic.dto.cat.shards;


public class ShardsRecord {
  private final String index;
  private final String shard;
  private final String prirep;
  private final String state;
  private final String mergesCurrent;
  private final String node;

  public ShardsRecord(String index, String shard, String prirep, String state, String mergesCurrent, String node) {
    this.index = index;
    this.shard = shard;
    this.prirep = prirep;
    this.state = state;
    this.mergesCurrent = mergesCurrent;
    this.node = node;
  }

  public String getIndex() {
    return index;
  }

  public String getShard() {
    return shard;
  }

  public String getPrirep() {
    return prirep;
  }

  public String getState() {
    return state;
  }

  public String getMergesCurrent() {
    return mergesCurrent;
  }

  public String getNode() {
    return node;
  }
}
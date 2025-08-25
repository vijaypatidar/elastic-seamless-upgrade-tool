package co.hyperflex.clients.elastic.dto.cat.shards;


public class ShardsRecord {
  private String index;
  private String shard;
  private String prirep;
  private String state;
  private String mergesCurrent;
  private String node;

  public void setIndex(String index) {
    this.index = index;
  }

  public void setShard(String shard) {
    this.shard = shard;
  }

  public void setPrirep(String prirep) {
    this.prirep = prirep;
  }

  public void setState(String state) {
    this.state = state;
  }

  public void setMergesCurrent(String mergesCurrent) {
    this.mergesCurrent = mergesCurrent;
  }

  public void setNode(String node) {
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
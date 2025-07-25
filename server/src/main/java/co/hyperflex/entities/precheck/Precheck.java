package co.hyperflex.entities.precheck;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.Date;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Base Precheck class with polymorphic serialization.
 */
@Document(collection = "prechecks")
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes({@JsonSubTypes.Type(value = ClusterPrecheck.class, name = "CLUSTER"),
    @JsonSubTypes.Type(value = NodePrecheck.class, name = "NODE"),
    @JsonSubTypes.Type(value = IndexPrecheck.class, name = "INDEX")})
public abstract class Precheck {
  @Id
  private String id;

  private String name;

  private PrecheckType type;

  private PrecheckSeverity severity = PrecheckSeverity.WARNING;

  private String precheckGroupId;

  private String clusterUpgradeJobId;

  private PrecheckStatus status = PrecheckStatus.PENDING;

  private List<String> logs = List.of();

  private Date startedAt;

  private Date endAt;


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public PrecheckType getType() {
    return type;
  }

  public void setType(PrecheckType type) {
    this.type = type;
  }

  public PrecheckSeverity getSeverity() {
    return severity;
  }

  public void setSeverity(PrecheckSeverity severity) {
    this.severity = severity;
  }

  public String getPrecheckGroupId() {
    return precheckGroupId;
  }

  public void setPrecheckGroupId(String precheckGroupId) {
    this.precheckGroupId = precheckGroupId;
  }

  public String getClusterUpgradeJobId() {
    return clusterUpgradeJobId;
  }

  public void setClusterUpgradeJobId(String clusterUpgradeJobId) {
    this.clusterUpgradeJobId = clusterUpgradeJobId;
  }

  public PrecheckStatus getStatus() {
    return status;
  }

  public void setStatus(PrecheckStatus status) {
    this.status = status;
  }

  public List<String> getLogs() {
    return logs;
  }

  public void setLogs(List<String> logs) {
    this.logs = logs;
  }

  public Date getStartedAt() {
    return startedAt;
  }

  public void setStartedAt(Date startedAt) {
    this.startedAt = startedAt;
  }

  public Date getEndAt() {
    return endAt;
  }

  public void setEndAt(Date endAt) {
    this.endAt = endAt;
  }
}


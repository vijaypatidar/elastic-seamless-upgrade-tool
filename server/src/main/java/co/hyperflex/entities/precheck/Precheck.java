package co.hyperflex.entities.precheck;

import co.hyperflex.entities.ansible.AnsiblePlaybookRun;
import co.hyperflex.entities.cluster.ClusterNode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "prechecks")
public class Precheck {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne
  @JoinColumn(name = "cluster_node_id")
  private ClusterNode node;


  @ManyToOne(optional = false)
  @JoinColumn(name = "precheck_job_id")
  private PrecheckJob precheckJob;

  @ManyToOne(optional = false)
  @JoinColumn(name = "precheck_definition_id")
  private PrecheckDefinition precheckDefinition;

  @OneToOne
  @JoinColumn(name = "ansible_run_id")
  private AnsiblePlaybookRun ansiblePlaybookRun;

  @OneToOne
  @JoinColumn(name = "java_run_id")
  private JavaPrecheckRun javaPrecheckRun;

  @Enumerated(EnumType.STRING)
  private PrecheckStatus status;

}

import { ElasticClient } from "../../../clients/elastic.client";
import { BaseClusterPrecheck } from "../../base/base-cluster-precheck";
import { ExecutionMode, PrecheckType } from "../../types/enums";
import { PrecheckExecutionRequest, ClusterContext, PrecheckSeverity } from "../../types/interfaces";

export class ClusterAllocationSettingPrecheck extends BaseClusterPrecheck {
	constructor() {
		super({
			id: "elasticsearch_cluster_allocation_setting_check",
			name: "Cluster allocation setting check",
			type: PrecheckType.CLUSTER,
			mode: ExecutionMode.CODE,
			severity: PrecheckSeverity.WARNING,
		});
	}

	protected async runForContext(request: PrecheckExecutionRequest<ClusterContext>): Promise<void> {
		const client = await ElasticClient.buildClient(request.cluster.clusterId);
		const es = client.getClient();

		const settings = await es.cluster.getSettings({
			flat_settings: true,
			include_defaults: false,
		});

		const allocationSetting =
			settings.transient?.["cluster.routing.allocation.enable"] ??
			settings.persistent?.["cluster.routing.allocation.enable"] ??
			"all"; // default fallback

		const message = `Current setting 'cluster.routing.allocation.enable' is '${allocationSetting}'.`;

		await this.addLog(request, message);

		if (["primaries", "none"].includes(allocationSetting)) {
			throw new Error(
				`Precheck failed: ${message} This setting may prevent shard allocation and lead to red cluster status. Set it to 'all' before upgrade.`
			);
		}
	}
}

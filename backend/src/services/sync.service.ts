import { ClusterNode, ClusterNodeType } from "../models/cluster-node.model";
import { KibanaClient } from "../clients/kibana.client";
import logger from "../logger/logger";
import { IElasticSearchInfo } from "../models/elastic-search-info.model";
import { clusterStatus, NodeStatus } from "../enums";
import { ElasticClient } from "../clients/elastic.client";
import { createOrUpdateElasticSearchInfo } from "./elastic-search-info.service";
import { NotificationEventType, notificationService, NotificationType } from "./notification.service";
import { clusterUpgradeJobService } from "./cluster-upgrade-job.service";
import { ClusterUpgradeJobStatus } from "../models/cluster-upgrade-job.model";

export const syncKibanaNodes = async (clusterId: string) => {
	try {
		const kibanaClient = await KibanaClient.buildClient(clusterId);
		const kibanaNodes = await ClusterNode.find({ clusterId: clusterId, type: ClusterNodeType.KIBANA });
		for (const kibanaNode of kibanaNodes) {
			const { version } = await kibanaClient.getKibanaNodeDetails(kibanaNode.ip);
			await ClusterNode.findOneAndUpdate(
				{ nodeId: kibanaNode.nodeId, type: ClusterNodeType.KIBANA },
				{ version: version },
				{
					new: true,
					runValidators: true,
					upsert: true,
				}
			);
		}
	} catch (error: any) {
		logger.debug(`Error syncing Kibana nodes for cluster ${clusterId}:`, error.message);
	}
};

export const syncElasticSearchInfo = async (clusterId: string) => {
	try {
		const client = await ElasticClient.buildClient(clusterId);
		const clusterDetails = await client.getClient().info();
		const indicesResponse = await client.getClient().cat.indices({ format: "json" });
		const healthDetails = await client.getClient().cluster.health();
		const setting = await client.getSetting();
		const adaptiveReplicaEnabled =
			setting.transient?.["search.adaptive_replica_selection"] ??
			setting.persistent?.["search.adaptive_replica_selection"] ??
			setting.defaults?.["search.adaptive_replica_selection"];

		const masterNodes = await client.getMasterNodes();
		const elasticSearchInfo: IElasticSearchInfo = {
			clusterName: clusterDetails?.cluster_name ?? null,
			clusterUUID: clusterDetails?.cluster_uuid ?? null,
			status: healthDetails.status as clusterStatus,
			version: clusterDetails.version.number,
			timedOut: healthDetails?.timed_out,
			numberOfDataNodes: healthDetails?.number_of_data_nodes ?? null,
			numberOfMasterNodes: masterNodes.length,
			numberOfNodes: healthDetails?.number_of_nodes ?? null,
			activePrimaryShards: healthDetails?.active_primary_shards ?? null,
			activeShards: healthDetails?.active_shards ?? null,
			unassignedShards: healthDetails?.unassigned_shards ?? null,
			initializingShards: healthDetails?.initializing_shards ?? null,
			relocatingShards: healthDetails?.relocating_shards ?? null,
			clusterId: clusterId,
			adaptiveReplicationEnabled: adaptiveReplicaEnabled,
			totalIndices: indicesResponse.length,
			lastSyncedAt: new Date(),
			currentMasterNode: masterNodes.filter((node) => node).map((node) => `${node.node}/${node.id}`)[0],
		};
		await createOrUpdateElasticSearchInfo(elasticSearchInfo);
	} catch (err) {
		throw new Error("Unable to sync with Elastic search instance! Maybe the connection is breaked");
	}
};

const syncClusterUpgradeJobStatus = async (clusterId: string) => {
	try {
		const job = await clusterUpgradeJobService.getActiveClusterUpgradeJobByClusterId(clusterId);
		if (!(job.status === ClusterUpgradeJobStatus.COMPLETED || job.status === ClusterUpgradeJobStatus.FAILED)) {
			const nodes = await ClusterNode.find({ clusterId: clusterId });
			const isUpgraded = nodes
				.map((node) => node.status === NodeStatus.AVAILABLE && job.targetVersion === node.version)
				.reduce((acc, curr) => acc && curr, true);
			if (isUpgraded) {
				job.status = ClusterUpgradeJobStatus.COMPLETED;
				clusterUpgradeJobService.updateClusterUpgradeJob(
					{ jobId: job.jobId },
					{ status: ClusterUpgradeJobStatus.COMPLETED }
				);
			}
		}
	} catch (error) {
		logger.debug(`No active upgrade job found for cluster ${clusterId}:`, error);
	}
};

notificationService.addNotificationListner(async (event) => {
	if (
		event.type === NotificationEventType.NOTIFICATION &&
		event.notificationType === NotificationType.SUCCESS &&
		event.clusterId
	) {
		await syncKibanaNodes(event.clusterId);
		await syncClusterUpgradeJobStatus(event.clusterId);
	}
});

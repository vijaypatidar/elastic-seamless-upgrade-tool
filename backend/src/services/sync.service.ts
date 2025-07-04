import { ClusterNode, ClusterNodeType } from "../models/cluster-node.model";
import { KibanaClient } from "../clients/kibana.client";
import logger from "../logger/logger";
import { IElasticSearchInfo } from "../models/elastic-search-info.model";
import { clusterStatus } from "../enums";
import { ElasticClient } from "../clients/elastic.client";
import { createOrUpdateElasticSearchInfo } from "./elastic-search-info.service";

export const syncKibanaNodes = async (clusterId: string) => {
	const kibanaClient = await KibanaClient.buildClient(clusterId);
	try {
		const kibanaNodes = await ClusterNode.find({ clusterId: clusterId, type: ClusterNodeType.KIBANA });
		for (const kibanaNode of kibanaNodes) {
			const { version, os, roles } = await kibanaClient.getKibanaNodeDetails();
			kibanaNode.version = version;
			kibanaNode.os = os;
			kibanaNode.roles = roles;
			await ClusterNode.findOneAndUpdate(
				{ nodeId: kibanaNode.nodeId, type: ClusterNodeType.KIBANA },
				kibanaNode,
				{
					new: true,
					runValidators: true,
					upsert: true,
				}
			);
		}
	} catch (error: any) {
		logger.error(`Error syncing Kibana nodes: ${error.message}`);
		throw new Error(`Error syncing Kibana nodes ${error.message}`);
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

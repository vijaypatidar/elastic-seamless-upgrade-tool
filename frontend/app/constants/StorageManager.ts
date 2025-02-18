const StorageManager = {
	SESSION_NAME: "session",
	SETUP_SET: "setup-step", // stored in session
	CLUSTER_ID: "cluster-id",
	INFRA_TYPE: "infrastructure-type",

	CLUSTER_ADDED: "cluster-added",
	DEPRECATION_PAGE_ALLOWED: "deprecation-page-allowed",
	ELASTIC_NODE_UPGRADE_ALLOWED: "elastic-node-upgrade-allowed",
	KIBANA_NODE_UPGRADE_ALLOWED: "kibana-node-upgrade-allowed",
	UPGRADE_ASSIST_ALLOWED: "upgrade-assist-allowed",
} as const

export default StorageManager

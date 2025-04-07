export enum ClusterType {
	KIBANA = "KIBANA",
	ELASTIC = "ELASTIC",
}

export enum AnsibleRequestType {
	UPGRADE = "UPGRADE",
	PRECHECK = "PRECHECK",
}

export enum AnsibleTaskStatus {
	STARTED = "STARTED",
	SUCCESS = "SUCCESS",
	FAILED = "FAILED",
}

export enum NodeStatus {
	UPGRADING = "UPGRADING",
	AVAILABLE = "AVAILABLE",
	UPGRADED = "UPGRADED",
	FAILED = "FAILED",
}

export enum clusterStatus {
	green = "green",
	yellow = "yellow",
	red = "red",
}
export enum PrecheckStatus {
	PENDING = "PENDING",
	RUNNING = "RUNNING",
	FAILED = "FAILED",
	COMPLETED = "COMPLETED",
}

export const mapAnsibleToPrecheckStatus = (ansibleStatus: AnsibleTaskStatus): PrecheckStatus => {
	switch (ansibleStatus) {
		case AnsibleTaskStatus.STARTED:
			return PrecheckStatus.RUNNING;
		case AnsibleTaskStatus.SUCCESS:
			return PrecheckStatus.COMPLETED;
		case AnsibleTaskStatus.FAILED:
			return PrecheckStatus.FAILED;
		default:
			throw new Error(`Unknown Ansible status: ${ansibleStatus}`);
	}
};

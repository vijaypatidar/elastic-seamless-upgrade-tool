type TPrecheckStatus = "PENDING" | "RUNNING" | "FAILED" | "COMPLETED"

type TCheckTab = "CLUSTER" | "NODES" | "INDEX" | "BREAKING_CHANGES"

type SEVERITY = "ERROR" | "WARNING" | "INFO"

type TNodeData = {
	nodeId: string
	ip: string
	name: string
	status: TPrecheckStatus
	prechecks: TPrecheck[]
}

type TGroupedPrecheck = {
	id: string
	name: string
	severity: SEVERITY
	status: TPrecheckStatus
	prechecks: TPrecheck[]
}

type TPrecheck = {
	id: string
	name: string
	status: TPrecheckStatus
	duration: string
	logs: string[]
	startTime: string
	endTime?: string
}

type TIndexData = {
	index: string
	name: string
	status: TPrecheckStatus
	prechecks: TPrecheck[]
}

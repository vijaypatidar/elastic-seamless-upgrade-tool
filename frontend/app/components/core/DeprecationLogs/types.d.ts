type DeprecationColumnType = Array<{
	key: string
	label: string
	align: "start" | "center" | "end" | undefined
	width: number
}>

type DeprecationRowType = {
	key: string
	status: string
	issue: string
	issue_details: string
	resolution: string | string[]
}

type DeprecationClusterType = { clusterType: "ELASTIC" | "KIBANA" }

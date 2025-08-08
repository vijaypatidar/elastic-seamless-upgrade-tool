type TDeprecationColumn = Array<{
	key: string
	label: string
	align: "start" | "center" | "end" | undefined
	width: number
}>

type TDeprecationRow = {
	key: string
	status: string
	issue: string
	issue_details: string
	resolutions: string[]
}

type TDeprecationCluster = { clusterType: "ELASTIC" | "KIBANA" }

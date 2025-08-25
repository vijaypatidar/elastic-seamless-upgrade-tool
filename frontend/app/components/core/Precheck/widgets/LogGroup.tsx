import { Box } from "@mui/material"
import { useMutation } from "@tanstack/react-query"
import { useMemo } from "react"
import BreakingChangesLogs from "./BreakingChanges"
import ClusterLogs from "./Cluster"
import IndexLogs from "./Index"
import NodesLogs from "./Nodes"
import axiosJSON from "~/apis/http"
import { useLocalStore } from "~/store/common"
import { toast } from "sonner"

function LogGroup({
	dataFor,
	data,
	isLoading,
	refetchData,
}: {
	dataFor: TCheckTab
	data: any
	isLoading: boolean
	refetchData: any
}) {
	const clusterId = useLocalStore((state: any) => state.clusterId)

	const { mutate: HandleRerun, isPending } = useMutation({
		mutationKey: ["handle-rerun"],
		mutationFn: async (payload: string) => {
			await axiosJSON
				.post(`/clusters/${clusterId}/prechecks/rerun`, payload)
				.then((res: any) => {
					console.log(res)
					refetchData()
				})
				.catch((err: any) => console.log(err))
		},
	})

	const handlePrecheckSkip = async (id: string, skip: boolean) => {
		try {
			await axiosJSON.put(`/clusters/${clusterId}/prechecks/${id}/skip?skip=${skip}`)
			toast.success(`Precheck ${skip ? "skipped" : "unskipped"} successfully`)
		} catch (err) {
			toast.error(`Failed to ${skip ? "skip" : "unskip"} precheck`)
		}
	}

	const layout = useMemo(() => {
		console.log(dataFor)
		if (dataFor === "CLUSTER") {
			return (
				<ClusterLogs
					data={data}
					handleRerun={(payload) => HandleRerun(payload)}
					handlePrecheckSkip={handlePrecheckSkip}
					isPending={isPending}
					isLoading={isLoading}
				/>
			)
		} else if (dataFor === "NODES") {
			return (
				<NodesLogs
					data={data}
					handleRerun={(payload) => HandleRerun(payload)}
					handlePrecheckSkip={handlePrecheckSkip}
					isPending={isPending}
					isLoading={isLoading}
				/>
			)
		} else if (dataFor === "INDEX") {
			return (
				<IndexLogs
					data={data}
					handleRerun={(payload) => HandleRerun(payload)}
					handlePrecheckSkip={handlePrecheckSkip}
					isPending={isPending}
					isLoading={isLoading}
				/>
			)
		} else if (dataFor === "BREAKING_CHANGES") {
			return <BreakingChangesLogs />
		}
	}, [dataFor, data])

	return <Box className="flex flex-row gap-[10px] w-full h-[calc(var(--window-height)-185px)]">{layout}</Box>
}

export default LogGroup

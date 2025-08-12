import { Box, Typography } from "@mui/material"
import { useQuery } from "@tanstack/react-query"
import axiosJSON from "~/apis/http"
import { useLocalStore } from "~/store/common"
import { useRealtimeEventListener } from "~/lib/hooks/useRealtimeEventListener"
import { LogsBreadcrumb } from "~/components/core/UpgradeLogs/LogsBreadcrumb"
import { FullScreenDrawer } from "~/components/utilities/FullScreenDrawer"

function useUpgradeLogs(nodeId: string) {
	const clusterId = useLocalStore((state: any) => state.clusterId)
	const fetchUpgradeLogs = async () => {
		const res = await axiosJSON.get("/upgrades/logs", { params: { clusterId, nodeId } })
		return res.data.logs ?? []
	}
	const {
		refetch,
		data: logs,
		isLoading,
	} = useQuery({
		queryKey: ["getUpgradeLogs", clusterId, nodeId],
		queryFn: fetchUpgradeLogs,
		staleTime: 0,
	})

	useRealtimeEventListener("UPGRADE_PROGRESS_CHANGE", () => refetch(), true)
	return { logs, isLoading, refetch }
}

function LogsList({ logs }: { logs: string[] }) {
	return (
		<Box className="flex w-full flex-col gap-2 overflow-scroll">
			{logs.map((log, index) => (
				<Box
					key={index}
					className="flex w-full flex-row items-start gap-[18px]"
					sx={{ padding: "2px 10px", borderRadius: "4px" }}
				>
					<Typography minWidth="28px" textAlign="right" fontFamily="Roboto Mono" fontSize="13px">
						{index + 1}
					</Typography>
					<Typography color="#E5E0E0" fontFamily="Roboto Mono" fontSize="13px">
						{log}
					</Typography>
				</Box>
			))}
		</Box>
	)
}

function UpgradeLogs({ onOpenChange, node }: { node: TUpgradeRow; onOpenChange: () => void }) {
	const { logs } = useUpgradeLogs(node.id)

	return (
		<FullScreenDrawer isOpen={true} onOpenChange={onOpenChange}>
			<Box minHeight="58px" />
			<Box className="flex items-center gap-3 justify-between">
				<LogsBreadcrumb onBack={onOpenChange} />
			</Box>
			<Box
				className="flex p-px rounded-2xl h-[calc(var(--window-height)-120px)]"
				sx={{ background: "radial-gradient(#6E687C, #1D1D1D)" }}
			>
				<Box className="flex flex-col rounded-2xl bg-[#0D0D0D] w-full h-full items-start">
					<Box
						className="flex flex-col h-full w-full gap-3 overflow-auto items-center"
						padding="16px 24px 16px 24px"
					>
						<Box className="flex w-full flex-row items-start gap-[18px]">
							<Typography
								color="#E5E0E0"
								fontFamily="Manrope"
								fontSize="14px"
								fontWeight="600"
								lineHeight="20px"
							>
								{node.node_name}
							</Typography>
						</Box>
						{logs && <LogsList logs={logs} />}
					</Box>
				</Box>
			</Box>
		</FullScreenDrawer>
	)
}

export default UpgradeLogs

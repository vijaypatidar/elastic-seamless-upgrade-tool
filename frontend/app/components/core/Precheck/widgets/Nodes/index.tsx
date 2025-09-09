import { Skeleton } from "@heroui/react"
import { Box, Typography } from "@mui/material"
import { ArrowCircleRight2, Refresh } from "iconsax-react"
import { useEffect, useState } from "react"
import { OutlinedBorderButton } from "~/components/utilities/Buttons"
import ListLoader from "../../loading/ListLoader"
import LogsList from "../LogsList"
import NoData from "../NoData"
import NodeListItem from "../NodeListItem"
import Switch from "~/components/utilities/Switch"

function NodesLogs({
	data,
	handleRerun,
	handlePrecheckSkip,
	isPending = false,
	isLoading = false,
}: {
	data: any
	handleRerun: (payload: any) => void
	handlePrecheckSkip: (id: string, skip: boolean) => void
	isPending: boolean
	isLoading: boolean
}) {
	const [selectedNode, setSelectedNode] = useState<any>(null)
	const [selectedPrecheck, setSelectedPrecheck] = useState<any>(null)

	useEffect(() => {
		if (data?.node?.length > 0) {
			if (selectedNode === null) {
				setSelectedNode(data?.node?.[0])
			} else {
				setSelectedNode(data?.node?.find((node: any) => node.nodeId === selectedNode?.nodeId))
			}
		} else {
			setSelectedNode(null)
		}
	}, [data])

	useEffect(() => {
		if (selectedPrecheck !== null) {
			setSelectedPrecheck(selectedNode?.prechecks?.find((p: any) => p.id === selectedPrecheck?.id))
		} else {
			setSelectedPrecheck(null)
		}
	}, [selectedNode])

	const handleNodeRerun = () => {
		handleRerun({
			nodeIds: [selectedNode.nodeId],
		})
	}

	const handlePrecheckRerun = () => {
		handleRerun({
			precheckIds: [selectedPrecheck.id],
		})
	}

	if (data?.node?.length === 0 && !isLoading) {
		return (
			<Box className="h-full p-px rounded-2xl w-full" sx={{ background: "radial-gradient(#6E687C, #1D1D1D)" }}>
				<Box className="flex w-full h-full flex-col gap-[6px] p-4 bg-[#0D0D0D] rounded-[15px]">
					<Box className="flex flex-row items-center gap-2 justify-between">
						<Typography
							color="#A6A6A6"
							fontFamily="Manrope"
							fontSize="12px"
							fontWeight="400"
							lineHeight="normal"
							letterSpacing="0.24px"
						>
							Prechecks
						</Typography>
						<OutlinedBorderButton onClick={handleNodeRerun} disabled={isPending || isLoading}>
							<Refresh color="currentColor" size="14px" />
							{isPending ? "Running..." : "Rerun"}
						</OutlinedBorderButton>
					</Box>
					<Box className="flex flex-col gap-1">
						<NoData
							title="No nodes available to display"
							subtitle="There are no nodes to show at the moment."
						/>
					</Box>
				</Box>
			</Box>
		)
	}

	return (
		<>
			<Box className="h-full flex flex-col w-1/3 gap-[6px]">
				<Typography
					marginTop="12px"
					color="#A9AAB6"
					fontFamily="Manrope"
					fontSize="12px"
					fontWeight="500"
					lineHeight="normal"
					letterSpacing="0.12px"
				>
					Nodes
				</Typography>
				<Box className="flex flex-col gap-1 overflow-x-scroll">
					{!isLoading ? (
						data?.node.map((node: any, index: number) => (
							<NodeListItem
								key={index}
								status={node?.status}
								severity={node?.severity}
								isSelected={selectedNode?.nodeId === node.nodeId}
								name={node?.name}
								onClick={() => setSelectedNode(node)}
							/>
						))
					) : (
						<ListLoader />
					)}
				</Box>
			</Box>
			<Box className="h-full p-px rounded-2xl w-1/3" sx={{ background: "radial-gradient(#6E687C, #1D1D1D)" }}>
				<Box className="flex h-full w-full flex-col gap-[6px] p-4 bg-[#0D0D0D] rounded-[15px] ">
					<Box className="flex flex-row justify-between gap-2">
						<Typography
							color="#A6A6A6"
							fontFamily="Manrope"
							fontSize="12px"
							fontWeight="400"
							lineHeight="normal"
							letterSpacing="0.24px"
						>
							Prechecks
						</Typography>
						<OutlinedBorderButton
							onClick={handleNodeRerun}
							disabled={isPending || isLoading || selectedNode === null}
						>
							<Refresh color="currentColor" size="14px" />
							{isPending ? "Running..." : "Rerun"}
						</OutlinedBorderButton>
					</Box>
					<Box className="flex flex-col gap-1">
						<Box className="flex flex-col px-3 py-[14px] gap-1">
							{!isLoading ? (
								selectedNode?.length !== 0 ? (
									selectedNode?.prechecks.map((change: any, index: number) => (
										<NodeListItem
											key={index}
											status={change.status}
											name={change.name}
											severity={change.severity}
											isSelected={change.id === selectedPrecheck?.id}
											onClick={() => setSelectedPrecheck(change)}
											duration={`${change.duration}`}
										/>
									))
								) : (
									<NoData
										title="No Prechecks available to display"
										subtitle="There are no prechecks to show right now."
									/>
								)
							) : (
								<ListLoader />
							)}
						</Box>
					</Box>
				</Box>
			</Box>

			<Box className="flex p-px rounded-2xl w-2/3" sx={{ background: "radial-gradient(#6E687C, #1D1D1D)" }}>
				<Box className="flex w-full h-full flex-col gap-4 py-4 px-6 bg-[#0D0D0D] rounded-[15px]">
					{selectedNode ? (
						<>
							<Box className="flex flex-row justify-between gap-2">
								<Box className="flex flex-col">
									<Typography
										color="#A6A6A6"
										fontFamily="Manrope"
										fontSize="12px"
										fontWeight="400"
										lineHeight="normal"
										letterSpacing="0.24px"
									>
										Logs
									</Typography>
									{!isLoading ? (
										<Typography
											color="#A9AAB6"
											fontFamily="Manrope"
											fontSize="14px"
											fontWeight="500"
											lineHeight="20px"
										>
											{selectedPrecheck?.name}
										</Typography>
									) : (
										<Skeleton className="rounded-md">
											<Box width="280px" height="20px" />
										</Skeleton>
									)}
								</Box>
								<Box className="flex flex-row gap-2">
									<Switch
										checked={selectedPrecheck?.severity === "SKIPPED"}
										onChange={(skip) => handlePrecheckSkip(selectedPrecheck.id, skip)}
										label="Skip"
										disabled={isPending || isLoading}
									/>
									<OutlinedBorderButton
										onClick={handlePrecheckRerun}
										disabled={isPending || isLoading || selectedPrecheck === null}
									>
										<Refresh color="currentColor" size="14px" />
										{isPending ? "Running..." : "Rerun"}
									</OutlinedBorderButton>
								</Box>
							</Box>
							<Box className="flex flex-col w-full gap-[2px]">
								<LogsList logs={selectedPrecheck?.logs || []} isLoading={isLoading} />
							</Box>
						</>
					) : (
						<NoData
							title="No logs available to display"
							subtitle="There are no logs to show at the moment."
						/>
					)}
				</Box>
			</Box>
		</>
	)
}

export default NodesLogs

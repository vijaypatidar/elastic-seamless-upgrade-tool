import { Skeleton } from "@heroui/react"
import { Box, Typography } from "@mui/material"
import { ArrowCircleRight2, Refresh } from "iconsax-react"
import { useEffect, useState } from "react"
import { OutlinedBorderButton } from "~/components/utilities/Buttons"
import ListLoader from "../../loading/ListLoader"
import LogsList from "../LogsList"
import NoData from "../NoData"
import NodeListItem from "../NodeListItem"

function IndexLogs({
	data,
	handleRerun,
	isPending = false,
	isLoading = false,
	handlePrecheckSkip,
}: {
	data: any
	handleRerun: (payload: any) => void
	handlePrecheckSkip: (id: string) => void
	isPending: boolean
	isLoading: boolean
}) {
	const [selectedIndex, setSelectedIndex] = useState<any>(null)
	const [selectedPrecheck, setSelectedPrecheck] = useState<any>(null)

	useEffect(() => {
		if (data?.index?.length > 0) {
			if (selectedIndex === null) {
				setSelectedIndex(data?.index[0])
			} else {
				setSelectedIndex(data?.index?.find((index: any) => index.index === selectedIndex.index))
			}
		} else {
			setSelectedIndex(null)
		}
	}, [data])

	useEffect(() => {
		if (selectedIndex !== null) {
			if (selectedPrecheck !== null) {
				const prev = selectedIndex?.prechecks?.find((precheck: any) => precheck.id === selectedPrecheck.id)
				setSelectedPrecheck(prev ? prev : selectedIndex?.prechecks?.[0])
			} else {
				setSelectedPrecheck(selectedIndex?.prechecks?.[0])
			}
		} else {
			setSelectedPrecheck(null)
		}
	}, [selectedIndex])

	const handleIndexRerun = () => {
		handleRerun({
			indexNames: [selectedIndex.index],
		})
	}

	const handlePrecheckRerun = () => {
		handleRerun({
			precheckIds: [selectedPrecheck.id],
		})
	}

	if (data?.index?.length === 0 && !isLoading) {
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
						<OutlinedBorderButton onClick={handleIndexRerun} disabled={isPending || isLoading}>
							<Refresh color="currentColor" size="14px" />
							{isPending ? "Running..." : "Rerun"}
						</OutlinedBorderButton>
					</Box>
					<Box className="flex flex-col gap-1">
						<NoData
							title="No index available to display"
							subtitle="There are no indexes to show right now."
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
					Indexes
				</Typography>
				<Box className="flex flex-col gap-1">
					{!isLoading ? (
						data?.index?.map((index: any, idx: number) => (
							<NodeListItem
								key={idx}
								status={index?.status}
								severity={index?.severity}
								isSelected={selectedIndex?.index === index.index}
								name={index?.name}
								onClick={() => setSelectedIndex(index)}
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
						<OutlinedBorderButton onClick={handleIndexRerun} disabled={isPending || isLoading}>
							<Refresh color="currentColor" size="14px" />
							{isPending ? "Running..." : "Rerun"}
						</OutlinedBorderButton>
					</Box>
					<Box className="flex flex-col gap-1">
						<Box className="flex flex-col px-3 py-[14px] gap-1">
							{!isLoading ? (
								selectedIndex ? (
									selectedIndex?.prechecks?.map((change: any, index: number) => (
										<NodeListItem
											key={index}
											status={change.status}
											severity={change.severity}
											name={change.name}
											isSelected={change.id === selectedPrecheck?.id}
											onClick={() => setSelectedPrecheck(change)}
											duration={`${change.duration}`}
										/>
									))
								) : (
									<NoData
										title="No Prechecks available to display"
										subtitle="Everything looks stable—no breaking changes detected."
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
					{selectedIndex ? (
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
									<OutlinedBorderButton
										onClick={handlePrecheckRerun}
										disabled={isPending || isLoading || selectedPrecheck === null}
									>
										<Refresh color="currentColor" size="14px" />
										{isPending ? "Running..." : "Rerun"}
									</OutlinedBorderButton>
									<OutlinedBorderButton
										onClick={() => handlePrecheckSkip(selectedPrecheck.id)}
										disabled={isPending || isLoading || selectedPrecheck === null}
									>
										<ArrowCircleRight2 color="currentColor" size="14px" />
										Skip
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

export default IndexLogs

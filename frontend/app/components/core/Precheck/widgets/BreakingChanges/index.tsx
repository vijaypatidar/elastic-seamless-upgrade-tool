import { Skeleton } from "@heroui/react"
import { Box, Typography } from "@mui/material"
import { useEffect, useState } from "react"
import ListLoader from "../../loading/ListLoader"
import LogsList from "../LogsList"
import NoData from "../NoData"
import NodeListItem from "../NodeListItem"

function BreakingChangesLogs({ data, isLoading = true }: { data: any; isLoading: boolean }) {
	const [selectedCheck, setSelectedCheck] = useState<any>(null)

	useEffect(() => {
		if (
			selectedCheck === null ||
			data?.breakingChanges?.filter((change: any) => change.id === selectedCheck?.id).length === 0
		) {
			if (data?.breakingChanges?.length === 0) {
				setSelectedCheck(null)
			} else {
				setSelectedCheck(data?.breakingChanges?.[0])
			}
		}
	}, [data])

	if (data.breakingChanges.length === 0 && !isLoading) {
		return (
			<Box className="h-full p-px rounded-2xl w-full" sx={{ background: "radial-gradient(#6E687C, #1D1D1D)" }}>
				<Box className="flex w-full h-full flex-col gap-[6px] p-4 bg-[#0D0D0D] rounded-[15px]">
					<Typography
						color="#A6A6A6"
						fontFamily="Manrope"
						fontSize="12px"
						fontWeight="400"
						lineHeight="normal"
						letterSpacing="0.24px"
					>
						Breaking changes
					</Typography>
					<Box className="flex flex-col gap-1">
						<NoData
							title="No Prechecks available to display"
							subtitle="Everything looks stable—no breaking changes detected."
						/>
					</Box>
				</Box>
			</Box>
		)
	}

	return (
		<>
			<Box className="h-full p-px rounded-2xl w-1/3" sx={{ background: "radial-gradient(#6E687C, #1D1D1D)" }}>
				<Box className="flex w-full h-full flex-col gap-[6px] p-4 bg-[#0D0D0D] rounded-[15px]">
					<Typography
						color="#A6A6A6"
						fontFamily="Manrope"
						fontSize="12px"
						fontWeight="400"
						lineHeight="normal"
						letterSpacing="0.24px"
					>
						Breaking changes
					</Typography>
					<Box className="flex flex-col gap-1">
						<Box className="flex flex-col px-3 py-[14px] gap-1">
							{!isLoading ? (
								data.breakingChanges.length !== 0 ? (
									data.breakingChanges.map((change: any, index: number) => {
										return (
											<NodeListItem
												key={index}
												status={change.status}
												name={change.name}
												isSelected={true}
												onClick={() => setSelectedCheck(change)}
											/>
										)
									})
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
								{selectedCheck?.name}
							</Typography>
						) : (
							<Skeleton className="rounded-md">
								<Box width="280px" height="20px" />
							</Skeleton>
						)}
					</Box>
					<Box className="flex flex-col w-full gap-[2px]">
						<LogsList logs={selectedCheck?.logs ?? []} isLoading={isLoading} />
					</Box>
				</Box>
			</Box>
		</>
	)
}

export default BreakingChangesLogs

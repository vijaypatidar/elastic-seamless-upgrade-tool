import { Skeleton } from "@heroui/react";
import { Box, Typography } from "@mui/material";
import { useMemo } from "react";
import NoData from "./NoData";

function LogsList({ logs, isLoading }: { logs: any; isLoading: boolean }) {
	const isLogEmpty = useMemo(() => logs.length === 0, [logs])

	if (isLogEmpty && !isLoading) {
		return <NoData title="No logs available to display" subtitle="There are no logs to show at the moment." />
	}

	return !isLoading ? (
		logs?.map((item: string, index: number) => {
			return (
				<Box
					key={index}
					className="flex w-full flex-row items-start gap-[18px]"
					sx={{
						":hover": { background: "#28282A", color: "#A08AF7" },
						padding: "4px 10px 4px 10px",
						borderRadius: "4px",
					}}
				>
					<Typography
						minWidth="28px"
						textAlign="right"
						fontFamily="Roboto Mono"
						fontSize="13px"
						fontStyle="normal"
						fontWeight="400"
						lineHeight="20px"
					>
						{index + 1}
					</Typography>
					<Typography
						color="#E5E0E0"
						fontFamily="Roboto Mono"
						fontSize="13px"
						fontWeight="400"
						lineHeight="20px"
					>
						{item}
					</Typography>
				</Box>
			)
		})
	) : (
		<>
			<Skeleton className="rounded-md">
				<Box height="28px" width="600px" />
			</Skeleton>
			<Skeleton className="rounded-md">
				<Box height="28px" width="600px" />
			</Skeleton>
			<Skeleton className="rounded-md">
				<Box height="28px" width="600px" />
			</Skeleton>
			<Skeleton className="rounded-md">
				<Box height="28px" width="600px" />
			</Skeleton>
			<Skeleton className="rounded-md">
				<Box height="28px" width="600px" />
			</Skeleton>
			<Skeleton className="rounded-md">
				<Box height="28px" width="600px" />
			</Skeleton>
		</>
	)
}

export default LogsList

import { Skeleton } from "@heroui/react"
import { Box } from "@mui/material"
import React from "react"

export function OneLineSkeleton({
	show,
	component,
	height,
	className = "rounded-lg",
}: {
	show: boolean
	component: React.ReactNode
	height: string
	className?: string
}) {
	return (
		<Box className="flex">
			{show ? (
				<Skeleton className={className}>
					<Box height={height}></Box>
				</Skeleton>
			) : (
				component
			)}
		</Box>
	)
}

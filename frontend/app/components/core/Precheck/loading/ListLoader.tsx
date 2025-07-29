import { Skeleton } from "@heroui/react"
import { Box } from "@mui/material"
import { useMemo } from "react"

function ListLoader() {
	return useMemo(
		() => (
			<>
				<Skeleton className="rounded-lg">
					<Box height="44px"/>
				</Skeleton>
				<Skeleton className="rounded-lg">
					<Box height="44px" />
				</Skeleton>
				<Skeleton className="rounded-lg">
					<Box height="44px" />
				</Skeleton>
			</>
		),
		[]
	)
}

export default ListLoader

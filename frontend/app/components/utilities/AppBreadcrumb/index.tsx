import { Box, Breadcrumbs, Typography } from "@mui/material"
import { type ReactNode } from "react"
import { ArrowRight2 } from "iconsax-react"

export interface BreadcrumbItem {
	label: string
	onClick?: () => void
	icon?: ReactNode
	color?: string
}

interface AppBreadcrumbProps {
	items: BreadcrumbItem[]
	separator?: ReactNode
}

function AppBreadcrumb({ items, separator = <ArrowRight2 color="#ADADAD" size="14px" /> }: AppBreadcrumbProps) {
	return (
		<Box className="flex border border-solid border-[#2F2F2F] w-max py-[6px] px-[10px] rounded-lg bg-[#141415]">
			<Breadcrumbs separator={separator}>
				{items.map((item, idx) => (
					<Typography
						key={idx}
						className={`flex items-center gap-[6px] ${item.onClick ? "cursor-pointer" : ""}`}
						color={item.color ?? "#ADADAD"}
						fontSize="12px"
						fontWeight="500"
						lineHeight="normal"
						onClick={item.onClick}
					>
						{item.icon}
						{item.label}
					</Typography>
				))}
			</Breadcrumbs>
		</Box>
	)
}

export default AppBreadcrumb

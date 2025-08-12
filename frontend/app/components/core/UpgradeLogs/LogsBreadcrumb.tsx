import { Box, Breadcrumbs, Typography } from "@mui/material"
import { ArrowLeft, ArrowRight2 } from "iconsax-react"

export const LogsBreadcrumb = ({ onBack }: { onBack: () => void }) => {
	return (
		<Box className="flex border border-solid border-[#2F2F2F] w-max py-[6px] px-[10px] rounded-lg bg-[#141415]">
			<Breadcrumbs separator={<ArrowRight2 color="#ADADAD" size="14px" />}>
				<Typography
					className="flex items-center gap-[6px] cursor-pointer"
					color="#ADADAD"
					fontSize="12px"
					fontWeight="500"
					onClick={onBack}
				>
					<ArrowLeft size="14px" color="currentColor" /> Go back
				</Typography>
				<Typography color="#BDA0FF" fontSize="12px" fontWeight="500">
					Logs
				</Typography>
			</Breadcrumbs>
		</Box>
	)
}

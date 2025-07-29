import { Box, Typography } from "@mui/material"
import { Folder } from "iconsax-react"

function NoData({ title, subtitle, marginTop = "90px" }: { title: string; subtitle: string; marginTop?: string }) {
	return (
		<Box className="flex flex-col items-center h-full w-full gap-4">
			<Box className="flex items-center justify-center bg-[#1A1A1A] rounded-[10px] size-12" marginTop={marginTop}>
				<Folder size="24px" color="#ADADAD" />
			</Box>
			<Box className="flex flex-col items-center gap-[5px]">
				<Typography
					color="#F1F0F0"
					textAlign="center"
					fontFamily="Manrope"
					fontSize="16px"
					fontWeight="400"
					lineHeight="18px"
					letterSpacing="0.32px"
				>
					{title}
				</Typography>
				<Typography
					color="#A6A6A6"
					textAlign="center"
					fontFamily="Manrope"
					fontSize="12px"
					fontWeight="400"
					lineHeight="normal"
					letterSpacing="0.24px"
				>
					{subtitle}
				</Typography>
			</Box>
		</Box>
	)
}

export default NoData

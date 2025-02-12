import { Box, Typography } from "@mui/material"

function Issue({ title, icon: Icon, value, fontColor = "#E87D65", bgColor = "rgba(206, 98, 75, 0.13)" }: TIssue) {
	return (
		<Box
			className="flex items-center justify-between gap-1 rounded-[32px] bg-[#121212] w-full"
			padding="10px 20px 10px 12px"
		>
			<Typography
				sx={{
					padding: "5px 7px",
					background: bgColor,
				}}
				className="flex flex-row items-center gap-1 rounded-3xl"
				color={fontColor}
				fontFamily="Inter"
				fontSize="12px"
				fontWeight="500"
				lineHeight="normal"
			>
				<Icon size="14px" color="currentColor" /> {title}
			</Typography>
			<Typography color="#FFF" fontSize="18px" fontWeight="600" lineHeight="normal">
				{value}
			</Typography>
		</Box>
	)
}

export default Issue

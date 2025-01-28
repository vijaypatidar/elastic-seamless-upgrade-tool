import { Box, Typography } from "@mui/material"

function MajorFeature({ icon, title }: { icon: string; title: string }) {
	return (
		<Box
			className="flex rounded-2xl relative items-stretch justify-center p-px"
			boxShadow="0px 0px 15px 2px rgba(120, 80, 213, 0.03)"
		>
			<Box className="flex flex-col gap-6 py-6 w-full mt-[1px] mb-[-1px] px-[28px] rounded-2xl bg-[#0d0d0d] z-10">
				<img src={icon} height="32px" width="32px" />
				<Typography color="#FFF" fontSize="16px" fontWeight="600" lineHeight="normal">
					{title}
				</Typography>
			</Box>
			<Box
				className="flex rounded-2xl p-px w-full h-full absolute z-0"
				sx={{
					background:
						"linear-gradient(135deg, #BBADDC 2.29%, #C9C0DF 44.53%, #131315 97.18%, #131315 97.18%)",
					animation: "gradient-border-breath 8s ease infinite",
				}}
			/>
		</Box>
	)
}

export default MajorFeature

import { Box, Typography } from "@mui/material"
import { Flash } from "iconsax-react"
import React from "react"
import { toast } from "sonner"

const TOAST_STYLES: {
	[Key: string]: {
		boxBorderGradient: string
		icon: React.ReactNode
	}
} = {
	SUCCESS: {
		boxBorderGradient: "linear-gradient(175deg, #27A56A 0%, #C0DFCF 30%, #131514 100%)",
		icon: (
			<Box
				className="flex p-px rounded-lg"
				sx={{
					background:
						"linear-gradient(135deg, #27A56A 2.29%, #C0DFCF 44.53%, #131315 97.18%, #131315 97.18%)",
				}}
			>
				<Box className="flex items-center rounded-lg justify-center min-w-[30px] min-h-[30px] bg-[#101010]">
					<Flash size="20px" color="#FFF" variant="Bold" />
				</Box>
			</Box>
		),
	},
}

function Toast({ varient, msg }: { varient: "ERROR" | "SUCCESS" | "WARNING"; msg: string }) {
	return toast(
		<Box
			className="flex p-[0.4px] w-full rounded-[14px]"
			sx={{ background: TOAST_STYLES[varient].boxBorderGradient }}
		>
			<Box className="flex items-center w-full bg-[#010101] flex-row gap-6 py-[14px] px-[26px] rounded-[14px]">
				{TOAST_STYLES[varient].icon}
				<Typography color="#FFF" fontSize="16px" fontWeight="600" lineHeight="normal">
					{msg}
				</Typography>
			</Box>
		</Box>,
		{
			duration: 1000000,
			closeButton: false,
			unstyled: true,
			className: "w-full",
			position: "bottom-center",
			style: {
				width :"100%"
			}

		}
	)
}

export default Toast

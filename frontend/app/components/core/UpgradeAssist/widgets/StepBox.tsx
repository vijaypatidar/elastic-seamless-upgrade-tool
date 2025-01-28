import { Box, Typography } from "@mui/material"
import React from "react"
import { getGradientClass } from "~/lib/Utils"

type TStepBox = {
	lastNode?: boolean
	currentStepStatus?: "COMPLETED" | "PENDING" | "INPROGRESS" | "NOTVISITED"
	nextStepStatus?: "COMPLETED" | "PENDING" | "INPROGRESS" | "NOTVISITED"
	boxBackground: string
	background: string
	boxShadow: string
	internalBackground: string
	textColor: string
	stepValue: string | React.ReactNode
	children: React.ReactNode
}

function StepBox({
	lastNode = false,
	currentStepStatus = "NOTVISITED",
	nextStepStatus = "NOTVISITED",
	boxBackground,
	background,
	boxShadow,
	internalBackground,
	textColor,
	stepValue,
	children,
}: TStepBox) {
	return (
		<li
			className={`relative w-full ${
				!lastNode &&
				`after:content-[''] after:w-[1px] after:h-full after:inline-block after:absolute after:-bottom-[60px] after:left-11 after:z-20  ${getGradientClass(
					currentStepStatus,
					nextStepStatus
				)}`
			} `}
		>
			<Box className="flex items-center justify-center gap-8 w-full">
				<Box
					className="rounded-[20px] p-px w-full"
					sx={{
						background: boxBackground,
					}}
				>
					<Box
						className="flex items-start gap-3.5 bg-[#0f0f0f] rounded-[20px] relative w-full"
						padding="20px 20px 20px 24px"
					>
						<Box
							className="rounded-full flex items-center justify-center p-px z-30"
							sx={{
								background: background,
								boxShadow: boxShadow,
							}}
						>
							<Box
								className="flex items-center justify-center rounded-full min-h-[38px] min-w-[38px] max-w-[38px] max-h-[38px]"
								sx={{ background: internalBackground }}
							>
								<Typography
									color={textColor}
									textAlign="center"
									fontSize="14px"
									fontWeight="600"
									lineHeight="22px"
								>
									{stepValue}
								</Typography>
							</Box>
						</Box>
						{children}
					</Box>
				</Box>
			</Box>
		</li>
	)
}

export default StepBox

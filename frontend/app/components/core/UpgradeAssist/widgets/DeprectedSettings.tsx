import { Box, Typography } from "@mui/material"
import { Alarm } from "iconsax-react"
import { FiAlertTriangle, FiArrowUpRight } from "react-icons/fi"
import { OutlinedBorderButton } from "~/components/utilities/Buttons"
import Issue from "./Issue"
import { Link } from "react-router"

function DeprectedSettings({ title, criticalValue, warningValue, isDisabled, to }: TDeprecationSettings) {
	return (
		<Box
			className="flex p-px w-full rounded-[20px]"
			sx={{
				background: "radial-gradient(#6E687C, #1D1D1D)",
			}}
		>
			<Box className="flex flex-row gap-[32px] rounded-[20px] w-full bg-[#0d0d0d]" padding="16px 16px 20px 24px">
				<Box className="flex flex-col gap-[10px] rounded-[20px] w-full bg-[#0d0d0d]">
					<Typography
						color="#9F9F9F"
						fontSize="14px"
						fontWeight="500"
						lineHeight="normal"
						letterSpacing="0.28px"
					>
						{title}
					</Typography>
					<Box className="flex flex-row gap-[14px] w-full">
						<Issue title="Critical" icon={FiAlertTriangle} value={criticalValue} />
						<Issue
							title="Warning"
							icon={Alarm}
							fontColor="#E0B517"
							bgColor="rgba(227, 192, 69, 0.13)"
							value={warningValue}
						/>
					</Box>
				</Box>
				<Box className="flex items-start">
					<OutlinedBorderButton
						component={Link}
						to={to}
						disabled={isDisabled}
						borderRadius="50%"
						sx={{ minWidth: "38px !important", minHeight: "38px !important", padding: "0px" }}
					>
						<FiArrowUpRight size="20px" color="#FFF" />
					</OutlinedBorderButton>
				</Box>
			</Box>
		</Box>
	)
}

export default DeprectedSettings

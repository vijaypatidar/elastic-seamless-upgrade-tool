import { Box, Typography } from "@mui/material"

const DetailBox = ({ title, description, customDescription }: DetailBoxType) => {
	return (
		<Box className="flex flex-col gap-1">
			<Typography color="#FFF" fontSize="12px" fontWeight="400" lineHeight="normal" letterSpacing="0.12px">
				{title}
			</Typography>
			{description && (
				<Typography color="#6E6E6E" fontSize="15px" fontWeight="400" lineHeight="20px" letterSpacing="0.3px">
					{description}
				</Typography>
			)}
			{customDescription ? customDescription : null}
		</Box>
	)
}

export default DetailBox

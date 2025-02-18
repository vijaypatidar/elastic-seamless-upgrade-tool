import { Box, Typography } from "@mui/material"
import { Link } from "react-router"
import { OutlinedBorderButton } from "~/components/utilities/Buttons"

function Page404() {
	return (
		<Box className="flex flex-col items-center justify-center h-[var(--window-height)] gap-3 bg-[#0A0A0A]">
			<Typography color="#FFF" fontSize="52px" fontWeight="600" lineHeight="normal">
				Page 404
			</Typography>
			<OutlinedBorderButton component={Link} to="/cluster-overview">Back to cluster overview</OutlinedBorderButton>
		</Box>
	)
}

export default Page404

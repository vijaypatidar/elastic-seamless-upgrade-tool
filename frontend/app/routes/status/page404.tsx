import { Box, Typography } from "@mui/material"
import { Link } from "react-router"

function Page404() {
	return (
		<Box className="flex flex-col items-center justify-center h-[var(--window-height)]">
			<Typography color="#FFF" fontSize="18px" fontWeight="600" lineHeight="24px">
				Page 404
			</Typography>
            <Link to="cluster-overview">Back to cluster overview</Link>
		</Box>
	)
}

export default Page404

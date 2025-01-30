import { Box } from "@mui/material"
import { Magicpen } from "iconsax-react"
import { Outlet } from "react-router"
import { OutlinedBorderButton } from "~/components/utilities/Buttons"
import AssetsManager from "~/constants/AssetsManager"

function Common() {
	return (
		<Box className="flex flex-col w-full pb-4" height="var(--window-height)">
			<Box className="flex flex-row gap-2 justify-between" padding="16px 32px 10px 40px">
				<img src={AssetsManager.LOGO_PLUS_NAMED} width="161.6px" height="36px" />
				<OutlinedBorderButton
					icon={Magicpen}
					filledIcon={Magicpen}
					iconProps={{ variant: "Bold" }}
					sx={{ ":hover": { color: "#F5BE3D !important" } }}
					gradient="linear-gradient(135deg,#E0B517 2.29%, #DFD8C0 44.53%, #151413 97.18%, #151413 97.18%)"
					boxShadow="0px 0px 19px 2px rgba(234, 180, 63, 0.41)"
					borderRadius="50px"
				>
					Upcoming features
				</OutlinedBorderButton>
			</Box>
			<Outlet />
		</Box>
	)
}

export default Common

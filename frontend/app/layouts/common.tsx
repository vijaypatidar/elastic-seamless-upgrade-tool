import { useDisclosure } from "@heroui/react"
import { Box } from "@mui/material"
import { Edit2, Magicpen } from "iconsax-react"
import { useMemo, useState } from "react"
import { Outlet } from "react-router"
import EditCluster from "~/components/core/EditCluster"
import UpcomingFeature from "~/components/core/UpcomingFeature"
import { OutlinedBorderButton } from "~/components/utilities/Buttons"
import AssetsManager from "~/constants/AssetsManager"

function Common() {
	const { isOpen, onOpen, onOpenChange } = useDisclosure()
	const { isOpen: isEditOpen, onOpen: onEditOpen, onOpenChange: onEditOpenChange } = useDisclosure()
	const [headerIndexChange, setHeaderIndexChange] = useState<boolean>(false)

	useMemo(() => {
		const timeout = !(isOpen || isEditOpen) ? 150 : 0
		const changeState = () => {
			setHeaderIndexChange(isOpen || isEditOpen)
		}
		setTimeout(() => {
			changeState()
		}, timeout)
	}, [isOpen, isEditOpen])

	return (
		<Box className="flex flex-col w-full pb-4 bg-[#0A0A0A]" height="var(--window-height)">
			<Box
				className="flex flex-row gap-2 justify-between bg-[#0A0A0A]"
				padding="16px 32px 10px 40px"
				zIndex={headerIndexChange ? "99999" : "0"}
			>
				<img src={AssetsManager.LOGO_PLUS_NAMED} width="161.6px" height="36px" />
				<Box className="flex flex-row gap-[6px] items-center">
					<OutlinedBorderButton
						icon={Edit2}
						filledIcon={Edit2}
						iconProps={{ variant: "Bold" }}
						sx={{ ":hover": { color: "#C3B7F5 !important" } }}
						gradient="linear-gradient(135deg, #6627FF 2.29%, #C9C0DF 44.53%, #151413 97.18%, #151413 97.18%)"
						boxShadow="0px 0px 19px 2px rgba(102, 39, 255, 0.41)"
						borderRadius="50px"
						onClick={onEditOpen}
					>
						Edit cluster
					</OutlinedBorderButton>
					<OutlinedBorderButton
						icon={Magicpen}
						filledIcon={Magicpen}
						iconProps={{ variant: "Bold" }}
						sx={{ ":hover": { color: "#F5BE3D !important" } }}
						gradient="linear-gradient(135deg,#E0B517 2.29%, #DFD8C0 44.53%, #151413 97.18%, #151413 97.18%)"
						boxShadow="0px 0px 19px 2px rgba(234, 180, 63, 0.41)"
						borderRadius="50px"
						onClick={onOpen}
					>
						Upcoming features
					</OutlinedBorderButton>
				</Box>
			</Box>
			<EditCluster isOpen={isEditOpen} onOpenChange={onEditOpenChange} />
			<UpcomingFeature isOpen={isOpen} onOpenChange={onOpenChange} />
			<Outlet />
		</Box>
	)
}

export default Common

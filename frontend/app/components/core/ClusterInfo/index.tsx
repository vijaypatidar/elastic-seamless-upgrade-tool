import { Box, Button, Menu, MenuItem, Typography } from "@mui/material"
import { ArrowDown2 } from "iconsax-react"
import PopupState, { bindMenu, bindTrigger } from "material-ui-popup-state"
import { OutlinedBorderButton } from "~/components/utilities/Buttons"
import DetailBox from "./widgets/DetailBox"
import Toast from "~/components/utilities/Toast"

const CLUSTER_STATUS_COLOR: { [key: string]: string } = {
	deploying: "#E0B517",
	deployed: "green",
	down: "gray",
}
const UPDATES = ["8.16", "8.19", "9.10"]

const STYLES = {
	MENU_ITEMS: {
		transition: "all 800ms",
		borderRadius: "6px",
		marginTop: "3px",
		padding: "6px 14px",
		color: "#898484",
		fontSize: "13px",
		fontWeight: "500",
		lineHeight: "20px",
	},
	MENU_PAPER: {
		style: {
			padding: "0px 6px",
			width: "156.73px",
			borderRadius: "8px",
			border: "1px solid #292929",
			background: "#121212",
		},
	},
	MENU_ROOT: {
		style: {
			top: "5px",
		},
	},
}

function ClusterInfo() {
	return (
		<Box
			className="flex p-px rounded-2xl w-full h-[calc(var(--window-height)-190px)]"
			sx={{
				background: "radial-gradient(#927CC5, #1D1D1D)",
			}}
		>
			<Box
				className="flex flex-col gap-6 rounded-2xl bg-[#0D0D0D] overflow-auto w-full"
				padding={{ xs: "14px 16px", md: "24px 32px" }}
			>
				<Box className="flex flex-row gap-3 justify-between">
					<Typography
						color="#FFF"
						fontSize="16px"
						fontWeight="600"
						lineHeight="normal"
						letterSpacing="0.16px"
					>
						Details
					</Typography>
					<PopupState variant="popover" popupId="demo-popup-menu">
						{(popupState) => (
							<Box className="relative">
								<OutlinedBorderButton {...bindTrigger(popupState)}>
									Update available <ArrowDown2 size="14px" color="#959595" />
								</OutlinedBorderButton>
								<Menu
									{...bindMenu(popupState)}
									transformOrigin={{
										vertical: "top",
										horizontal: "left",
									}}
									slotProps={{
										root: STYLES.MENU_ROOT,
										paper: STYLES.MENU_PAPER,
									}}
								>
									{UPDATES.map((update, index) => {
										return (
											<MenuItem key={index} sx={STYLES.MENU_ITEMS}>
												{update}
											</MenuItem>
										)
									})}
								</Menu>
							</Box>
						)}
					</PopupState>
				</Box>
				<Box className="flex flex-col gap-6 overflow-auto">
					<Box className="flex flex-col sm:flex-row gap-6 sm:gap-16">
						<Box className="flex flex-col gap-[24px] w-2/4">
							<DetailBox title="Cluster name" description="testcluster" />
							<DetailBox title="Cluster UUID" description="Csfjdskfjeuwibkjsndfjkdsnf" />
							<DetailBox title="Infrastructure type" description="Placeholder" />
						</Box>
						<Box className="flex flex-col gap-[24px] w-1/2">
							<DetailBox
								title="Cluster status"
								customDescription={
									<Box className="flex flex-row items-center gap-2">
										<Box
											component="span"
											className="flex min-w-[6px] min-h-[6px] rounded-[2px]"
											sx={{ background: CLUSTER_STATUS_COLOR["deploying"] }}
										/>
										<Typography
											color={CLUSTER_STATUS_COLOR["deploying"]}
											fontFamily="Inter"
											fontSize="12px"
											fontWeight="500"
											lineHeight="normal"
										>
											Yellow
										</Typography>
									</Box>
								}
							/>
							<DetailBox title="ES Version" description="8.12" />
							<DetailBox title="Timed out" description="False" />
						</Box>
					</Box>
					<Box
						className="grid col-auto grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-[14px] bg-[#161616] rounded-2xl"
						padding="14px 16px"
					>
						<DetailBox title="Number of data nodes" description="1" />
						<DetailBox title="Number of nodes" description="1" />
						<DetailBox title="Active primary shards" description="1" />
						<DetailBox title="Active shards" description="1" />
						<DetailBox title="Relocating shards" description="0" />
						<DetailBox title="Initializing shards" description="0" />
						<DetailBox title="Unassigned shards" description="1" />
					</Box>
				</Box>
			</Box>
		</Box>
	)
}

export default ClusterInfo

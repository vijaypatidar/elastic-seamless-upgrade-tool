import { Box, Menu, MenuItem, Typography } from "@mui/material"
import { useQuery } from "@tanstack/react-query"
import { ArrowDown2 } from "iconsax-react"
import _ from "lodash"
import PopupState, { bindMenu, bindTrigger } from "material-ui-popup-state"
import { toast } from "sonner"
import axiosJSON from "~/apis/http"
import { OutlinedBorderButton } from "~/components/utilities/Buttons"
import StorageManager from "~/constants/StorageManager"
import LocalStorageHandler from "~/lib/LocalHanlder"
import DetailBox from "./widgets/DetailBox"
import { OneLineSkeleton } from "~/components/utilities/Skeletons"
import { Skeleton } from "@heroui/react"

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
	const getClusterInfo = async () => {
		const clusterId = LocalStorageHandler.getItem(StorageManager.CLUSTER_ID) || "cluster-id"
		let response: any = []
		await axiosJSON
			.get(`/api/elastic/clusters/${clusterId}/info`)
			.then((res) => {
				console.log(res)
				response = res.data
			})
			.catch((err) => toast.error(err?.response?.data.err))
		return response
	}
	const { data, isLoading, refetch, isRefetching } = useQuery({ queryKey: ["cluster-info"], queryFn: getClusterInfo })

	const handleVersionSelect = async (ver: string) => {}

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
					<OneLineSkeleton
						className="rounded-[10px] max-w-[250px] w-[154px]"
						show={isLoading}
						component={
							<PopupState variant="popover" popupId="demo-popup-menu">
								{(popupState) => (
									<Box className="relative">
										<OutlinedBorderButton {...bindTrigger(popupState)} disabled={false}>
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
											{data?.possibleUpgradeVersions?.map((update: string, index: number) => {
												return (
													<MenuItem
														key={index}
														sx={STYLES.MENU_ITEMS}
														onClick={() => {
															popupState.close()
															handleVersionSelect(update)
														}}
													>
														{update}
													</MenuItem>
												)
											})}
										</Menu>
									</Box>
								)}
							</PopupState>
						}
						height="36px"
					/>
				</Box>
				<Box className="flex flex-col gap-6 overflow-auto">
					<Box className="flex flex-col sm:flex-row gap-6 sm:gap-16">
						<Box className="flex flex-col gap-[24px] w-2/4">
							<DetailBox title="Cluster name" description={data?.clusterName} isLoading={isLoading} />
							<DetailBox title="Cluster UUID" description={data?.clusterUUID} isLoading={isLoading} />
							<DetailBox
								title="Infrastructure type"
								description={_.capitalize(
									LocalStorageHandler.getItem(StorageManager.INFRA_TYPE) ?? "placeholder"
								)}
								isLoading={isLoading}
							/>
						</Box>
						<Box className="flex flex-col gap-[24px] w-1/2">
							<DetailBox
								title="Cluster status"
								customDescription={
									<Box className="flex flex-row items-center gap-2 h-[20px]">
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
								isLoading={isLoading}
							/>
							<DetailBox title="ES Version" description={data?.version} isLoading={isLoading} />
							<DetailBox
								title="Timed out"
								description={data?.timeOut ? "True" : "False"}
								isLoading={isLoading}
							/>
						</Box>
					</Box>
					<Box
						className="grid col-auto grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-[14px] bg-[#161616] rounded-2xl"
						padding="14px 16px"
					>
						<DetailBox
							title="Number of data nodes"
							description={data?.numberOfDataNodes}
							isLoading={isLoading}
						/>
						<DetailBox title="Number of nodes" description={data?.numberOfNodes} isLoading={isLoading} />
						<DetailBox
							title="Active primary shards"
							description={data?.activePrimaryShards}
							isLoading={isLoading}
						/>
						<DetailBox title="Active shards" description={data?.activeShards} isLoading={isLoading} />
						<DetailBox
							title="Relocating shards"
							description={data?.relocatingShards}
							isLoading={isLoading}
						/>
						<DetailBox
							title="Initializing shards"
							description={data?.initializingShards}
							isLoading={isLoading}
						/>
						<DetailBox
							title="Unassigned shards"
							description={data?.unassignedShards}
							isLoading={isLoading}
						/>
					</Box>
				</Box>
			</Box>
		</Box>
	)
}

export default ClusterInfo

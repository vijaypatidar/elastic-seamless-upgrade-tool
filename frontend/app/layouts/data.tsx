import { Box, CssBaseline } from "@mui/material"
import { useCallback } from "react"
import { Outlet } from "react-router"
import AssetsManager from "../constants/AssetsManager"

function DataLayout() {
	const getLogo = useCallback((position = "side") => {
		const posParams: any = {
			side: {
				logo: {
					width: "63.9px",
					height: "63.6px",
				},
				name: {
					width: "216px",
					height: "58px",
				},
			},
			top: {
				logo: {
					width: "44px",
					height: "44px",
				},
				name: {
					width: "119px",
					height: "32px",
				},
			},
		}

		return (
			<Box className="flex items-center gap-2">
				<Box className="flex flex-grow max-w-[64px] max-h-[64px]">
					<img src={AssetsManager.LOGO} {...posParams[position].logo} />
				</Box>
				<Box className="flex flex-grow max-h-[58px] max-w-[216px]">
					<img src={AssetsManager.NAMED_LOGO_TRANSPARENT} {...posParams[position].name} />
				</Box>
			</Box>
		)
	}, [])

	return (
		<Box className="flex flex-col md:flex-row w-full bg-neutral-950 px-0 pt-0 pb-3 md:py-3 md:px-4">
			<CssBaseline />
			<Box
				className="hidden md:flex flex-col justify-between w-full rounded-3xl relative flex-grow"
				padding="29px 24px 24px 29px"
				height="calc(var(--window-height) - 24px)"
				maxWidth="494px"
				sx={{
					background:
						"linear-gradient(170deg, #0A0A0A 7.63%, #090417 31.48%, #2B184C 46.15%, #533787 60.93%, #BDA0FF 87.43%, #F5F3FF 100%)",
				}}
			>
				<Box className="absolute bottom-0 left-0 mix-blend-color-dodge">
					<img src={AssetsManager.LAYOUT_LOGO_TRANSITION} width="428px" height="474px" />
				</Box>
				<Box className="absolute flex flex-shrink-0 bottom-0 left-0">
					<img src={AssetsManager.STARS_SMALL} width="693px" height="520px" />
				</Box>
				<Box className="absolute flex flex-shrink-0 bottom-0 left-0">
					<img src={AssetsManager.STARS_LARGE} width="693px" height="520px" />
				</Box>
				{getLogo("side")}
				<Box className="flex justify-end">
					<Box className="flex size-12 bg-neutral-950 rounded-lg"></Box>
				</Box>
			</Box>
			<Box className="flex md:hidden border-b border-solid border-gray-400 p-4">{getLogo("top")}</Box>
			<Box className="flex flex-grow w-full">
				<Outlet />
			</Box>
		</Box>
	)
}

export default DataLayout

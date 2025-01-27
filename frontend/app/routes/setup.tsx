import { Box, CssBaseline, Typography } from "@mui/material"
import Setup from "~/components/core/Setup"
import type { Route } from "./+types/home"

export function meta({}: Route.MetaArgs) {
	return [{ title: "Hyperflex" }, { name: "description", content: "Welcome to Hyperflex" }]
}

export default function SetupPage() {
	return (
		<Box className="w-full" padding={{ xs: "32px 16px", lg: "32px 56px 32px 152px" }}>
			<CssBaseline />
			<Box className="flex flex-col w-full gap-10">
				<Box className="flex flex-col gap-4 max-w-[515px] w-full">
					<Box
						className="flex items-center justify-center rounded-[10px] p-px w-min"
						sx={{
							background:
								"linear-gradient(135deg, #6627FF 2.29%, #C9C0DF 44.53%, #131315 97.18%, #131315 97.18%)",
						}}
					>
						<Typography className="flex items-center justify-center size-10 rounded-[9px] font-manrope font-semibold text-xl leading-[22px] bg-black">
							👋
						</Typography>
					</Box>
					<Typography fontSize="24px" fontWeight={600} lineHeight="22px" color="#FFF">
						Hello there, welcome back!
					</Typography>
				</Box>
				<Setup />
			</Box>
		</Box>
	)
}

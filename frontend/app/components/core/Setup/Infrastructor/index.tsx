import { Box, Typography } from "@mui/material"
import { ArrowRight, CloudNotif, Driver2 } from "iconsax-react"
import { useState } from "react"
import { ConatinedButton } from "~/components/utilities/Buttons"
import SelectionTile from "./widgets/SelectionTile"

function Infrastructor({ onSubmit }: { onSubmit: (value: string | number | null) => void }) {
	const [selected, setSelected] = useState<string | number | null>(null)

	const handleSubmit = () => {
		onSubmit(selected)
	}

	return (
		<Box
			className="flex flex-col gap-[3px]"
			height={{
				xs: "calc(var(--window-height) - 346px)",
				sm: "calc(var(--window-height) - 280px)",
				md: "calc(var(--window-height) - 280px)",
			}}
		>
			<Typography fontSize="14px" fontWeight={400} lineHeight="20px" color="#ABA9B1">
				Choose elastic infrastructure
			</Typography>
			<Box className="flex flex-col gap-3 justify-between h-full">
				<Box className="flex flex-col items-stretch gap-4 max-w-[515px] w-full">
					<SelectionTile
						Icon={CloudNotif}
						label="On cloud"
						isSelected={selected === "on-cloud"}
						value="on-cloud"
						onSelect={(value: string | number) => setSelected(value)}
					/>
					<SelectionTile
						Icon={Driver2}
						label="On premises"
						isSelected={selected === "on-premises"}
						value="on-premises"
						onSelect={(value: string | number) => setSelected(value)}
					/>
				</Box>
				<Box className="flex justify-end">
					<ConatinedButton disabled={!Boolean(selected)} onClick={handleSubmit}>
						Continue <ArrowRight size="20px" color="currentColor" />
					</ConatinedButton>
				</Box>
			</Box>
		</Box>
	)
}

export default Infrastructor

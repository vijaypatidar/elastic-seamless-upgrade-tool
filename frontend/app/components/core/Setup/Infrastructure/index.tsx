import { Box, Typography } from "@mui/material"
import { ArrowLeft, ArrowRight, CloudNotif, Driver2 } from "iconsax-react"
import { SiKubernetes } from "react-icons/si"
import { useState } from "react"
import { ConatinedButton, OutlinedButton } from "~/components/utilities/Buttons"
import SelectionTile from "./widgets/SelectionTile"

function Infrastructure({ onSubmit, backStep }: { onSubmit: (value: string | null) => void , backStep: () => void }) {
	const [selected, setSelected] = useState<string | null>(null)

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
						label="Elastic Cloud"
						isSelected={selected === "ELASTIC_CLOUD"}
						value="ELASTIC_CLOUD"
						onSelect={(value: string) => setSelected(value)}
					/>
					<SelectionTile
						Icon={Driver2}
						label="On-Prem/ Self managed Cluster"
						isSelected={selected === "SELF_MANAGED"}
						value="SELF_MANAGED"
						onSelect={(value: string) => setSelected(value)}
					/>
					<SelectionTile
						Icon={SiKubernetes}
						label="Elastic on Kubernetes"
						isSelected={selected === "ON_KUBERNETES"}
						value="ON_KUBERNETES"
						onSelect={(value: string) => setSelected(value)}
						comingSoon
					/>
				</Box>
				<Box className="flex justify-end">
					<OutlinedButton onClick={backStep}>
						<ArrowLeft size="20px" color="currentColor" /> Back
					</OutlinedButton>
					<ConatinedButton disabled={!Boolean(selected)} onClick={handleSubmit}>
						Continue <ArrowRight size="20px" color="currentColor" />
					</ConatinedButton>
				</Box>
			</Box>
		</Box>
	)
}

export default Infrastructure

import { Alarm, ArrowCircleRight2 } from "iconsax-react"
import { FiAlertTriangle } from "react-icons/fi"
import { usePrecheckSummary } from "~/lib/hooks/usePrecheckSummary"
import { Box } from "@mui/material"

type PrecheckType = "warning" | "critical" | "skipped"

const config: Record<
	PrecheckType,
	{ bg: string; color: string; label: string; Icon: React.ComponentType<{ size?: string; color?: string }> }
> = {
	warning: {
		bg: "bg-amber-300/10",
		color: "#E3C045",
		label: "Warning",
		Icon: Alarm,
	},
	critical: {
		bg: "bg-red-500/10",
		color: "#E87D65",
		label: "Critical",
		Icon: FiAlertTriangle,
	},
	skipped: {
		bg: "bg-[#98959E21]",
		color: "#98959E",
		label: "Skipped",
		Icon: ArrowCircleRight2,
	},
}

function PrecheckSummaryItem({ type, count }: { type: PrecheckType; count: number }) {
	const { bg, color, label, Icon } = config[type]

	return (
		<Box
			className={`px-[7px] py-[5px] ${bg} rounded-3xl inline-flex justify-center items-center gap-1 overflow-hidden`}
		>
			<Box className="flex justify-start items-center gap-1">
				<Icon size="14px" color={color} />
				<Box className="justify-start text-[12px] font-medium font-['Inter']" style={{ color }}>
					{label} {count}
				</Box>
			</Box>
		</Box>
	)
}

export function PrecheckSummary() {
	const precheckSummary = usePrecheckSummary()
	return (
		<Box className="flex items-center justify-center gap-[6px]">
			<PrecheckSummaryItem count={precheckSummary.warning} type="warning" />
			<PrecheckSummaryItem count={precheckSummary.critical} type="critical" />
			<PrecheckSummaryItem count={precheckSummary.skipped} type="skipped" />
		</Box>
	)
}

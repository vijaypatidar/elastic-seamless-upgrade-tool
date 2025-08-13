import { Alarm } from "iconsax-react"
import { FiAlertTriangle } from "react-icons/fi"

type PrecheckType = "warning" | "critical"

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
}

export function PrecheckSummary({ type, count }: { type: PrecheckType; count: number }) {
	const { bg, color, label, Icon } = config[type]

	return (
		<div className={`px-3 py-0 ${bg} rounded-3xl inline-flex justify-center items-center gap-1 overflow-hidden`}>
			<div className="flex justify-start items-center gap-1">
				<Icon size="14px" color={color} />
				<div className="justify-start text-xs font-medium font-['Inter']" style={{ color }}>
					{label} {count}
				</div>
			</div>
		</div>
	)
}

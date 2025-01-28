import { Box, Typography } from "@mui/material"
import { Record, TickCircle } from "iconsax-react"

const SelectionTile = ({
	value,
	Icon,
	label,
	isSelected,
	onSelect,
}: {
	value: string
	Icon: React.FunctionComponent<{ size: string; color: string }>
	label: string
	isSelected: boolean
	onSelect: (value: string | number) => void
}) => {
	return (
		<Box
			className="flex rounded-2xl p-px"
			sx={{
				background: isSelected
					? "linear-gradient(103deg, #393939 3.33%, #EBEAF0 53.63%, #6627FF 97.35%)"
					: "transparent",
				transition: "all 0.5s",
				":hover": {
					background: "linear-gradient(103deg, #393939 3.33%, #EBEAF0 53.63%, #6627FF 97.35%)",
					transition: "all 0.5s",
				},
			}}
		>
			<Box
				className="flex gap-2 w-full justify-between items-start py-6 pl-8 pr-7 border border-solid border-[#292929] cursor-pointer bg-neutral-950 rounded-[calc(1rem-1px)]"
				onClick={() => onSelect(value)}
				sx={{
					boxShadow: isSelected ? "0px 0px 16px 2px rgba(105, 56, 224, 0.33)" : "none",
					":hover #main-icon": { color: "#CCFE76 !important", transition: "all 0.5s" },
					":hover > #tick-icon": { color: "#5C5C5C !important", transition: "all 0.5s" },
				}}
			>
				<Box className="flex flex-col gap-[14px] ">
					<span id="main-icon" style={{ color: isSelected ? "#CCFE76" : "#FFF" }}>
						<Icon size="20px" color="currentColor" />
					</span>
					<Typography color="#FFF" fontSize="16px" fontWeight="500" lineHeight="22px">
						{label}
					</Typography>
				</Box>
				{isSelected ? (
					<TickCircle color="#CCFE76" variant="Bold" size="25px" />
				) : (
					<span id="tick-icon" style={{ color: "#292929" }}>
						<Record size="25px" color="currentColor" />
					</span>
				)}
			</Box>
		</Box>
	)
}

export default SelectionTile

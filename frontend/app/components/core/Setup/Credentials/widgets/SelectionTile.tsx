import { Box, Typography } from "@mui/material"
import { Record, TickCircle } from "iconsax-react"

function SelectionTile({ value, label, isSelected, onSelect }: TSelectionTile) {
	return (
		<Box
			className="flex rounded-2xl p-px w-full"
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
				className="flex gap-[10px] h-[54px] w-full items-center p-4 border border-solid border-[#292929] cursor-pointer bg-neutral-950 rounded-[calc(1rem-1px)]"
				onClick={() => onSelect(value)}
				sx={{
					boxShadow: isSelected ? "0px 0px 16px 2px rgba(105, 56, 224, 0.33)" : "none",
					transition: "all 0.5s",
					":hover > #selection-label": { color: "#FFF !important", transition: "all 0.5s" },
					":hover > #icon-hover": { color: "#5C5C5C !important", transition: "all 0.5s" },
				}}
			>
				{isSelected ? (
					<TickCircle color="#CCFE76" variant="Bold" size="24px" />
				) : (
					<span id="icon-hover" style={{ color: "#292929" }}>
						<Record color="currentColor" size="24px" />
					</span>
				)}
				<Typography
					id="selection-label"
					color={isSelected ? "#FFF" : "#A3A3A3"}
					fontSize="14px"
					fontWeight="400"
					lineHeight="22px"
				>
					{label}
				</Typography>
			</Box>
		</Box>
	)
}

export default SelectionTile
